package com.example.booking.service;

import com.example.booking.domain.Room;
import com.example.booking.dto.CreateRoomRequest;
import com.example.booking.exception.ResourceNotFoundException;
import com.example.booking.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class RoomService {

	private final RoomRepository roomRepository;

	public RoomService(RoomRepository roomRepository) {
		this.roomRepository = roomRepository;
	}

	@Transactional
	public Room create(CreateRoomRequest request) {
		Room room = Room.builder()
				.number(request.number())
				.type(request.type())
				.capacity(request.capacity())
				.pricePerNight(request.pricePerNight())
				.build();
		return roomRepository.save(room);
	}

	public Room getById(Long id) {
		return roomRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Room not found: " + id));
	}

	public List<Room> list() {
		return roomRepository.findAll();
	}

	@Transactional
	public void delete(Long id) {
		if (!roomRepository.existsById(id)) {
			throw new ResourceNotFoundException("Room not found: " + id);
		}
		roomRepository.deleteById(id);
	}
}
