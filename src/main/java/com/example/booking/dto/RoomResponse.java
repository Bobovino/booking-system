package com.example.booking.dto;

import com.example.booking.domain.Room;
import com.example.booking.domain.RoomType;

import java.math.BigDecimal;

public record RoomResponse(
		Long id,
		String number,
		RoomType type,
		Integer capacity,
		BigDecimal pricePerNight
) {
	public static RoomResponse from(Room room) {
		return new RoomResponse(room.getId(), room.getNumber(), room.getType(), room.getCapacity(), room.getPricePerNight());
	}
}
