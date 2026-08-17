package com.example.booking.dto;

import com.example.booking.domain.RoomType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateRoomRequest(
		@NotBlank String number,
		@NotNull RoomType type,
		@Min(1) Integer capacity,
		@NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal pricePerNight
) {
}
