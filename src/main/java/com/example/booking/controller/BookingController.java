package com.example.booking.controller;

import com.example.booking.dto.BookingResponse;
import com.example.booking.dto.CreateBookingRequest;
import com.example.booking.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

	private final BookingService bookingService;

	public BookingController(BookingService bookingService) {
		this.bookingService = bookingService;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public BookingResponse create(@Valid @RequestBody CreateBookingRequest request) {
		return BookingResponse.from(bookingService.create(request));
	}

	@GetMapping
	public List<BookingResponse> list() {
		return bookingService.list().stream().map(BookingResponse::from).toList();
	}

	@GetMapping("/{id}")
	public BookingResponse getById(@PathVariable Long id) {
		return BookingResponse.from(bookingService.getById(id));
	}

	@PostMapping("/{id}/cancel")
	public BookingResponse cancel(@PathVariable Long id) {
		return BookingResponse.from(bookingService.cancel(id));
	}
}
