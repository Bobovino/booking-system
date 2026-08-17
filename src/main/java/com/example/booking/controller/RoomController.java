package com.example.booking.controller;

import com.example.booking.domain.Room;
import com.example.booking.dto.CreateRoomRequest;
import com.example.booking.dto.RoomResponse;
import com.example.booking.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

	private final RoomService roomService;

	public RoomController(RoomService roomService) {
		this.roomService = roomService;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public RoomResponse create(@Valid @RequestBody CreateRoomRequest request) {
		return RoomResponse.from(roomService.create(request));
	}

	@GetMapping
	public List<RoomResponse> list() {
		return roomService.list().stream().map(RoomResponse::from).toList();
	}

	@GetMapping("/{id}")
	public RoomResponse getById(@PathVariable Long id) {
		return RoomResponse.from(roomService.getById(id));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		roomService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
