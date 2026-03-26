package com.example.PopcornCinema.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentTransactionResponse {
    private String orderCode;
    private BigDecimal amount;
    private String status;
    private String qrContent;
    private String qrImageUrl;
    private LocalDateTime expiresAt;

    public String getOrderCode() { return orderCode; }
    public BigDecimal getAmount() { return amount; }
    public String getStatus() { return status; }
    public String getQrContent() { return qrContent; }
    public String getQrImageUrl() { return qrImageUrl; }
    public LocalDateTime getExpiresAt() { return expiresAt; }

    public void setOrderCode(String orderCode) { this.orderCode = orderCode; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setStatus(String status) { this.status = status; }
    public void setQrContent(String qrContent) { this.qrContent = qrContent; }
    public void setQrImageUrl(String qrImageUrl) { this.qrImageUrl = qrImageUrl; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
}