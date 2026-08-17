package com.example.booking.repository;

import com.example.booking.domain.Booking;
import com.example.booking.domain.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

	@Query("""
			select b from Booking b
			where b.room.id = :roomId
			and b.status = :status
			and b.checkIn < :checkOut
			and b.checkOut > :checkIn
			""")
	List<Booking> findOverlapping(
			@Param("roomId") Long roomId,
			@Param("checkIn") LocalDate checkIn,
			@Param("checkOut") LocalDate checkOut,
			@Param("status") BookingStatus status
	);

	@Query("select b from Booking b join fetch b.room where b.id = :id")
	Optional<Booking> findByIdWithRoom(@Param("id") Long id);

	@Query("select b from Booking b join fetch b.room")
	List<Booking> findAllWithRoom();
}
