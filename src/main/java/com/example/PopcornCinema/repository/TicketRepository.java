package com.example.PopcornCinema.repository;

import com.example.PopcornCinema.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Optional<Ticket> findByBookingId(Long bookingId);

    List<Ticket> findAllByBookingId(Long bookingId);

    @Query("""
        SELECT t.seat.id
        FROM Ticket t
        WHERE t.booking.showtime.id = :showtimeId
    """)
    List<Long> findBookedSeatIds(@Param("showtimeId") Long showtimeId);

    @Query("""
        SELECT t.seatId
        FROM Ticket t
        WHERE t.showtimeId = :showtimeId
          AND UPPER(t.status) IN ('BOOKED', 'USED')
    """)
    List<Long> findSoldSeatIdsByShowtimeId(@Param("showtimeId") Long showtimeId);
}