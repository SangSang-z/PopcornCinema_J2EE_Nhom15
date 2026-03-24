package com.example.PopcornCinema.repository;

import com.example.PopcornCinema.entity.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {

    @Query("""
    SELECT s FROM Showtime s
    WHERE s.auditorium.id = :auditoriumId
    AND (:start < s.endTime AND :end > s.startTime)
    """)
    List<Showtime> findConflictingShowtimes(
            @Param("auditoriumId") Long auditoriumId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}