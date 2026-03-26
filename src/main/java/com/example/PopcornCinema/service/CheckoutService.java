package com.example.PopcornCinema.service;

import com.example.PopcornCinema.dto.CheckoutSummaryResponse;

public interface CheckoutService {
    CheckoutSummaryResponse getSummary(Long showtimeId, Long userId, Long promotionId);
}