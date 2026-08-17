package com.example.booking.integration;

import com.example.booking.domain.RoomType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class BookingIntegrationTest {

	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void bookingASecondTimeForOverlappingDatesReturnsConflict() throws Exception {
		Map<String, Object> roomRequest = Map.of(
				"number", "202",
				"type", RoomType.DOUBLE.name(),
				"capacity", 2,
				"pricePerNight", 80.0);

		String roomResponse = mockMvc.perform(post("/api/rooms")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(roomRequest)))
				.andExpect(status().isCreated())
				.andReturn().getResponse().getContentAsString();

		Long roomId = objectMapper.readTree(roomResponse).get("id").asLong();

		Map<String, Object> bookingRequest = Map.of(
				"roomId", roomId,
				"guestName", "Eve",
				"guestEmail", "eve@example.com",
				"checkIn", "2030-01-10",
				"checkOut", "2030-01-15");

		mockMvc.perform(post("/api/bookings")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(bookingRequest)))
				.andExpect(status().isCreated());

		Map<String, Object> overlappingRequest = Map.of(
				"roomId", roomId,
				"guestName", "Frank",
				"guestEmail", "frank@example.com",
				"checkIn", "2030-01-12",
				"checkOut", "2030-01-18");

		mockMvc.perform(post("/api/bookings")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(overlappingRequest)))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.status").value(409));
	}
}
