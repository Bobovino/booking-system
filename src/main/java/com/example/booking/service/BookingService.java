package com.example.booking.service;

import com.example.booking.domain.Booking;
import com.example.booking.domain.BookingStatus;
import com.example.booking.domain.Room;
import com.example.booking.dto.CreateBookingRequest;
import com.example.booking.exception.BookingConflictException;
import com.example.booking.exception.ResourceNotFoundException;
import com.example.booking.repository.BookingRepository;
import com.example.booking.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class BookingService {

	private final BookingRepository bookingRepository;
	private final RoomRepository roomRepository;

	public BookingService(BookingRepository bookingRepository, RoomRepository roomRepository) {
		this.bookingRepository = bookingRepository;
		this.roomRepository = roomRepository;
	}

	@Transactional
	public Booking create(CreateBookingRequest request) {
		if (!request.checkIn().isBefore(request.checkOut())) {
			throw new IllegalArgumentException("checkIn must be before checkOut");
		}

		Room room = roomRepository.findById(request.roomId())
				.orElseThrow(() -> new ResourceNotFoundException("Room not found: " + request.roomId()));

		List<Booking> overlapping = bookingRepository.findOverlapping(
				room.getId(), request.checkIn(), request.checkOut(), BookingStatus.CONFIRMED);
		if (!overlapping.isEmpty()) {
			throw new BookingConflictException(
					"Room " + room.getNumber() + " is already booked for the requested dates");
		}

		Booking booking = Booking.builder()
				.room(room)
				.guestName(request.guestName())
				.guestEmail(request.guestEmail())
				.checkIn(request.checkIn())
				.checkOut(request.checkOut())
				.status(BookingStatus.CONFIRMED)
				.createdAt(Instant.now())
				.build();

		return bookingRepository.save(booking);
	}

	public Booking getById(Long id) {
		return bookingRepository.findByIdWithRoom(id)
				.orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + id));
	}

	public List<Booking> list() {
		return bookingRepository.findAllWithRoom();
	}

	@Transactional
	public Booking cancel(Long id) {
		Booking booking = getById(id);
		booking.setStatus(BookingStatus.CANCELLED);
		return booking;
	}
}
