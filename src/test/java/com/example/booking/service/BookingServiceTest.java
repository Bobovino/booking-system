package com.example.booking.service;

import com.example.booking.domain.Booking;
import com.example.booking.domain.BookingStatus;
import com.example.booking.domain.Room;
import com.example.booking.dto.CreateBookingRequest;
import com.example.booking.exception.BookingConflictException;
import com.example.booking.exception.ResourceNotFoundException;
import com.example.booking.repository.BookingRepository;
import com.example.booking.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

	@Mock
	private BookingRepository bookingRepository;

	@Mock
	private RoomRepository roomRepository;

	@InjectMocks
	private BookingService bookingService;

	private Room room;

	@BeforeEach
	void setUp() {
		room = Room.builder().id(1L).number("101").capacity(2).pricePerNight(BigDecimal.TEN).build();
	}

	@Test
	void createsBookingWhenRoomIsFree() {
		CreateBookingRequest request = new CreateBookingRequest(
				1L, "Alice", "alice@example.com", LocalDate.now().plusDays(1), LocalDate.now().plusDays(3));

		when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
		when(bookingRepository.findOverlapping(any(), any(), any(), any())).thenReturn(List.of());
		when(bookingRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

		Booking result = bookingService.create(request);

		assertThat(result.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
		assertThat(result.getRoom()).isEqualTo(room);
	}

	@Test
	void rejectsOverlappingBooking() {
		CreateBookingRequest request = new CreateBookingRequest(
				1L, "Bob", "bob@example.com", LocalDate.now().plusDays(1), LocalDate.now().plusDays(3));

		when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
		when(bookingRepository.findOverlapping(any(), any(), any(), any()))
				.thenReturn(List.of(Booking.builder().id(99L).build()));

		assertThatThrownBy(() -> bookingService.create(request))
				.isInstanceOf(BookingConflictException.class);
	}

	@Test
	void rejectsUnknownRoom() {
		CreateBookingRequest request = new CreateBookingRequest(
				42L, "Carol", "carol@example.com", LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));

		when(roomRepository.findById(42L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> bookingService.create(request))
				.isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	void rejectsInvalidDateRange() {
		CreateBookingRequest request = new CreateBookingRequest(
				1L, "Dave", "dave@example.com", LocalDate.now().plusDays(3), LocalDate.now().plusDays(1));

		assertThatThrownBy(() -> bookingService.create(request))
				.isInstanceOf(IllegalArgumentException.class);
	}
}
