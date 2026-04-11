package com.example.PopcornCinema.repository;

import com.example.PopcornCinema.entity.Booking;
import com.example.PopcornCinema.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUser(User user);

    List<Booking> findByUser_EmailContainingOrUser_PhoneContaining(String email, String phone);

    Optional<Booking> findByBookingCode(String bookingCode);

    List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);

    long countByStatusIgnoreCase(String status);

    List<Booking> findByStatusIgnoreCaseAndCreatedAtBetween(String status,
                                                            LocalDateTime start,
                                                            LocalDateTime end);

    boolean existsByUserIdAndPromotionIdAndStatusIgnoreCase(Long userId, Long promotionId, String status);

    @Query("""
        SELECT DISTINCT b.promotionId
        FROM Booking b
        WHERE b.userId = :userId
          AND b.promotionId IN :promotionIds
          AND UPPER(b.status) = UPPER(:status)
    """)
    List<Long> findUsedPromotionIds(@Param("userId") Long userId,
                                    @Param("promotionIds") List<Long> promotionIds,
                                    @Param("status") String status);

    @Query("""
        SELECT COALESCE(SUM(b.totalAmount), 0)
        FROM Booking b
        WHERE UPPER(b.status) = UPPER(:status)
    """)
    BigDecimal sumTotalAmountByStatus(@Param("status") String status);
}
