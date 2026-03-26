package com.example.PopcornCinema.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentTransactionStatusResponse {
    private String orderCode;
    private String status;
    private BigDecimal amount;
    private LocalDateTime expiresAt;

    public String getOrderCode() { return orderCode; }
    public String getStatus() { return status; }
    public BigDecimal getAmount() { return amount; }
    public LocalDateTime getExpiresAt() { return expiresAt; }

    public void setOrderCode(String orderCode) { this.orderCode = orderCode; }
    public void setStatus(String status) { this.status = status; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
}