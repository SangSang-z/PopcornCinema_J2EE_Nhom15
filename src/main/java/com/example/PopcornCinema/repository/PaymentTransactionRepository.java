package com.example.PopcornCinema.repository;

import com.example.PopcornCinema.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    Optional<PaymentTransaction> findByOrderCode(String orderCode);

    List<PaymentTransaction> findByUserIdAndShowtimeIdAndStatus(Long userId, Long showtimeId, String status);

    @Query("""
        SELECT pt
        FROM PaymentTransaction pt
        WHERE pt.userId = :userId
          AND pt.showtimeId = :showtimeId
          AND pt.status IN :statuses
    """)
    List<PaymentTransaction> findByUserIdAndShowtimeIdAndStatuses(Long userId, Long showtimeId, Collection<String> statuses);

    List<PaymentTransaction> findAllByOrderByCreatedAtDesc();
}