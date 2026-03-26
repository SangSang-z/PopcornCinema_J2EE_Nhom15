package com.example.PopcornCinema.respository;

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
        SELECT t.seatId
        FROM Ticket t
        WHERE t.showtimeId = :showtimeId
          AND UPPER(t.status) IN ('BOOKED', 'USED')
    """)
    List<Long> findSoldSeatIdsByShowtimeId(@Param("showtimeId") Long showtimeId);
}