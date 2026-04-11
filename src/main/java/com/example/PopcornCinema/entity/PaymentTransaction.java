package com.example.PopcornCinema.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_transactions")
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_code", nullable = false, unique = true, length = 50)
    private String orderCode;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "showtime_id", nullable = false)
    private Long showtimeId;

    @Column(name = "promotion_id")
    private Long promotionId;

    @Column(name = "booking_id")
    private Long bookingId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "qr_content", length = 255)
    private String qrContent;

    @Column(name = "qr_image_url", length = 500)
    private String qrImageUrl;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    public PaymentTransaction() {
    }

    public Long getId() { return id; }
    public String getOrderCode() { return orderCode; }
    public Long getUserId() { return userId; }
    public Long getShowtimeId() { return showtimeId; }
    public Long getPromotionId() { return promotionId; }
    public Long getBookingId() { return bookingId; }
    public BigDecimal getAmount() { return amount; }
    public String getStatus() { return status; }
    public String getQrContent() { return qrContent; }
    public String getQrImageUrl() { return qrImageUrl; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public LocalDateTime getPaidAt() { return paidAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setId(Long id) { this.id = id; }
    public void setOrderCode(String orderCode) { this.orderCode = orderCode; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setShowtimeId(Long showtimeId) { this.showtimeId = showtimeId; }
    public void setPromotionId(Long promotionId) { this.promotionId = promotionId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setStatus(String status) { this.status = status; }
    public void setQrContent(String qrContent) { this.qrContent = qrContent; }
    public void setQrImageUrl(String qrImageUrl) { this.qrImageUrl = qrImageUrl; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
