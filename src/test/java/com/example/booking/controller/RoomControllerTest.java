package com.example.booking.controller;

import com.example.booking.domain.Room;
import com.example.booking.domain.RoomType;
import com.example.booking.exception.ResourceNotFoundException;
import com.example.booking.service.RoomService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoomController.class)
class RoomControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private RoomService roomService;

	@Test
	void createRoomReturns201() throws Exception {
		Room saved = Room.builder().id(1L).number("101").type(RoomType.SINGLE).capacity(1).pricePerNight(BigDecimal.valueOf(50)).build();
		when(roomService.create(any())).thenReturn(saved);

		String requestBody = """
				{"number": "101", "type": "SINGLE", "capacity": 1, "pricePerNight": 50.0}
				""";

		mockMvc.perform(post("/api/rooms")
						.contentType("application/json")
						.content(requestBody))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.number").value("101"));
	}

	@Test
	void createRoomWithBlankNumberReturns400() throws Exception {
		String requestBody = """
				{"number": "", "type": "SINGLE", "capacity": 1, "pricePerNight": 50.0}
				""";

		mockMvc.perform(post("/api/rooms")
						.contentType("application/json")
						.content(requestBody))
				.andExpect(status().isBadRequest());
	}

	@Test
	void getUnknownRoomReturns404() throws Exception {
		when(roomService.getById(eq(999L))).thenThrow(new ResourceNotFoundException("Room not found: 999"));

		mockMvc.perform(get("/api/rooms/999"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404));
	}
}
