package com.example.PopcornCinema.dto;

public class CreatePaymentTransactionRequest {
    private Long userId;
    private Long promotionId;

    public Long getUserId() { return userId; }
    public Long getPromotionId() { return promotionId; }

    public void setUserId(Long userId) { this.userId = userId; }
    public void setPromotionId(Long promotionId) { this.promotionId = promotionId; }
}