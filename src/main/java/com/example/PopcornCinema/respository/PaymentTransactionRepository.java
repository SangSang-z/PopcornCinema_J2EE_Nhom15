package com.example.PopcornCinema.respository;

import com.example.PopcornCinema.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    Optional<PaymentTransaction> findByOrderCode(String orderCode);
    List<PaymentTransaction> findByUserIdAndShowtimeIdAndStatus(Long userId, Long showtimeId, String status);
}