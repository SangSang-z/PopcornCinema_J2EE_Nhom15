package com.example.PopcornCinema.repository;

import com.example.PopcornCinema.entity.BookingCombo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface BookingComboRepository extends JpaRepository<BookingCombo, Long> {

    // ===== Combo tạm trước thanh toán =====
    List<BookingCombo> findByShowtimeIdAndUserIdAndBookingIdIsNullOrderByIdAsc(Long showtimeId, Long userId);

    Optional<BookingCombo> findByShowtimeIdAndUserIdAndComboIdAndBookingIdIsNull(
            Long showtimeId, Long userId, Long comboId
    );

    void deleteByShowtimeIdAndUserIdAndBookingIdIsNull(Long showtimeId, Long userId);

    @Query(value = """
        SELECT COALESCE(SUM(c.price * bc.quantity), 0)
        FROM booking_combos bc
        JOIN combos c ON bc.combo_id = c.id
        WHERE bc.showtime_id = :showtimeId
          AND bc.user_id = :userId
          AND bc.booking_id IS NULL
    """, nativeQuery = true)
    BigDecimal calculateTempComboTotal(
            @Param("showtimeId") Long showtimeId,
            @Param("userId") Long userId
    );

    // ===== Combo đã chốt theo booking =====
    List<BookingCombo> findByBookingIdOrderByIdAsc(Long bookingId);

    void deleteByBookingId(Long bookingId);

    @Query(value = """
        SELECT COALESCE(SUM(c.price * bc.quantity), 0)
        FROM booking_combos bc
        JOIN combos c ON bc.combo_id = c.id
        WHERE bc.booking_id = :bookingId
    """, nativeQuery = true)
    BigDecimal calculateBookedComboTotal(@Param("bookingId") Long bookingId);
}