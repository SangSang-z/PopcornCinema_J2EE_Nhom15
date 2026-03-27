package com.example.PopcornCinema.repository;

import com.example.PopcornCinema.entity.Seat;
import com.example.PopcornCinema.entity.SeatHold;
import com.example.PopcornCinema.entity.Showtime;
import com.example.PopcornCinema.repository.projection.HeldSeatSummaryProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SeatHoldRepository extends JpaRepository<SeatHold, Long> {

    boolean existsByShowtimeAndSeat(Showtime showtime, Seat seat);

    List<SeatHold> findByExpiresAtBefore(LocalDateTime time);

    List<SeatHold> findByUser_Id(Long userId);

    List<SeatHold> findAllByShowtimeIdAndUserId(Long showtimeId, Long userId);

    void deleteByShowtimeIdAndUserId(Long showtimeId, Long userId);

    @Query("""
        SELECT sh
        FROM SeatHold sh
        WHERE sh.showtimeId = :showtimeId
          AND sh.seatId = :seatId
          AND sh.expiresAt > :now
    """)
    List<SeatHold> findActiveHolds(
            @Param("showtimeId") Long showtimeId,
            @Param("seatId") Long seatId,
            @Param("now") LocalDateTime now
    );

    @Query("""
        SELECT sh
        FROM SeatHold sh
        WHERE sh.showtimeId = :showtimeId
          AND sh.seatId = :seatId
          AND sh.userId = :userId
          AND sh.expiresAt > :now
    """)
    Optional<SeatHold> findOwnActiveHold(
            @Param("showtimeId") Long showtimeId,
            @Param("seatId") Long seatId,
            @Param("userId") Long userId,
            @Param("now") LocalDateTime now
    );

    @Query("""
        SELECT sh
        FROM SeatHold sh
        WHERE sh.showtimeId = :showtimeId
          AND sh.expiresAt > :now
    """)
    List<SeatHold> findAllActiveByShowtimeId(
            @Param("showtimeId") Long showtimeId,
            @Param("now") LocalDateTime now
    );

    @Modifying
    @Query("""
        DELETE FROM SeatHold sh
        WHERE sh.showtimeId = :showtimeId
          AND sh.userId = :userId
          AND sh.seatId IN :seatIds
    """)
    void deleteHeldSeats(
            @Param("showtimeId") Long showtimeId,
            @Param("userId") Long userId,
            @Param("seatIds") List<Long> seatIds
    );

    @Modifying
    @Query("""
        DELETE FROM SeatHold sh
        WHERE sh.expiresAt <= :now
    """)
    void deleteExpired(@Param("now") LocalDateTime now);

    @Query(value = """
        SELECT
            se.id AS seatId,
            se.seat_row AS seatRow,
            se.seat_number AS seatNumber,
            se.extra_price AS extraPrice
        FROM seat_holds sh
        JOIN seats se ON sh.seat_id = se.id
        WHERE sh.showtime_id = :showtimeId
          AND sh.user_id = :userId
          AND sh.expires_at > NOW()
        ORDER BY se.seat_row, se.seat_number
    """, nativeQuery = true)
    List<HeldSeatSummaryProjection> findHeldSeatSummaryByShowtimeIdAndUserId(
            @Param("showtimeId") Long showtimeId,
            @Param("userId") Long userId
    );
}