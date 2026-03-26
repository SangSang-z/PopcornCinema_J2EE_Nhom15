package com.example.PopcornCinema.respository;

import com.example.PopcornCinema.entity.Showtime;
import com.example.PopcornCinema.respository.projection.CheckoutShowtimeProjection;
import com.example.PopcornCinema.respository.projection.SeatProjection;
import com.example.PopcornCinema.respository.projection.ShowtimeSeatMapProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.PopcornCinema.respository.projection.CheckoutShowtimeProjection;

import java.util.List;

public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {

    @Query(value = """
    SELECT
        s.id AS showtimeId,
        m.title AS movieTitle,
        m.poster_url AS posterUrl,
        m.age_rating AS ageRating,
        c.id AS cinemaId,
        c.name AS cinemaName,
        c.city AS city,
        a.id AS auditoriumId,
        a.name AS auditoriumName,
        s.start_time AS startTime,
        s.end_time AS endTime,
        s.base_price AS basePrice
    FROM showtimes s
    JOIN movies m ON s.movie_id = m.id
    JOIN auditoriums a ON s.auditorium_id = a.id
    JOIN cinemas c ON a.cinema_id = c.id
    WHERE s.id = :showtimeId
    """, nativeQuery = true)
ShowtimeSeatMapProjection findSeatMapInfoByShowtimeId(@Param("showtimeId") Long showtimeId);

    @Query(value = """
        SELECT
            se.id AS seatId,
            se.seat_row AS seatRow,
            se.seat_number AS seatNumber,
            se.seat_type AS seatType,
            se.extra_price AS extraPrice
        FROM showtimes s
        JOIN auditoriums a ON s.auditorium_id = a.id
        JOIN seats se ON se.auditorium_id = a.id
        WHERE s.id = :showtimeId
          AND se.status = 'ACTIVE'
        ORDER BY se.seat_row, se.seat_number
        """, nativeQuery = true)
    List<SeatProjection> findSeatsByShowtimeId(@Param("showtimeId") Long showtimeId);

    @Query(value = """
    SELECT
        s.id AS showtimeId,
        m.title AS movieTitle,
        m.poster_url AS posterUrl,
        m.age_rating AS ageRating,
        c.name AS cinemaName,
        a.name AS auditoriumName,
        s.start_time AS startTime,
        s.end_time AS endTime,
        s.base_price AS basePrice
    FROM showtimes s
    JOIN movies m ON s.movie_id = m.id
    JOIN auditoriums a ON s.auditorium_id = a.id
    JOIN cinemas c ON a.cinema_id = c.id
    WHERE s.id = :showtimeId
    """, nativeQuery = true)
CheckoutShowtimeProjection findCheckoutInfoByShowtimeId(@Param("showtimeId") Long showtimeId);
}