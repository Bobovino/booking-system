package com.example.booking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateBookingRequest(
		@NotNull Long roomId,
		@NotBlank String guestName,
		@NotBlank @Email String guestEmail,
		@NotNull @FutureOrPresent LocalDate checkIn,
		@NotNull @FutureOrPresent LocalDate checkOut
) {
}
