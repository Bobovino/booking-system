package com.example.booking.dto;

import com.example.booking.domain.Booking;
import com.example.booking.domain.BookingStatus;

import java.time.LocalDate;

public record BookingResponse(
		Long id,
		Long roomId,
		String roomNumber,
		String guestName,
		String guestEmail,
		LocalDate checkIn,
		LocalDate checkOut,
		BookingStatus status
) {
	public static BookingResponse from(Booking booking) {
		return new BookingResponse(
				booking.getId(),
				booking.getRoom().getId(),
				booking.getRoom().getNumber(),
				booking.getGuestName(),
				booking.getGuestEmail(),
				booking.getCheckIn(),
				booking.getCheckOut(),
				booking.getStatus()
		);
	}
}
