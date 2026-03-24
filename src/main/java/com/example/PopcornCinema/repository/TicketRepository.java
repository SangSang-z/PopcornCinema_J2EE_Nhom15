package com.example.PopcornCinema.repository;

import com.example.PopcornCinema.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    @Query("""
    SELECT t.seat.id 
    FROM Ticket t
    WHERE t.booking.showtime.id = :showtimeId
    """)
    List<Long> findBookedSeatIds(Long showtimeId);
}