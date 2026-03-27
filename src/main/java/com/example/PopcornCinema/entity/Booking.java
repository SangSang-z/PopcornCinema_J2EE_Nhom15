package com.example.PopcornCinema.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_code", nullable = false, unique = true)
    private String bookingCode;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "showtime_id", nullable = false)
    private Long showtimeId;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "promotion_id")
    private Long promotionId;

    @Column(name = "discount_amount")
    private BigDecimal discountAmount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "showtime_id", insertable = false, updatable = false)
    private Showtime showtime;

    public Booking() {
    }

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (status == null) status = "PENDING_PAYMENT";
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getBookingCode() { return bookingCode; }
    public Long getUserId() { return userId; }
    public Long getShowtimeId() { return showtimeId; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public Long getPromotionId() { return promotionId; }
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public User getUser() { return user; }
    public Showtime getShowtime() { return showtime; }

    public void setId(Long id) { this.id = id; }
    public void setBookingCode(String bookingCode) { this.bookingCode = bookingCode; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setShowtimeId(Long showtimeId) { this.showtimeId = showtimeId; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = BigDecimal.valueOf(totalAmount); }
    public void setStatus(String status) { this.status = status; }
    public void setPromotionId(Long promotionId) { this.promotionId = promotionId; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void setUser(User user) { this.user = user; this.userId = user != null ? user.getId() : null; }
    public void setShowtime(Showtime showtime) { this.showtime = showtime; this.showtimeId = showtime != null ? showtime.getId() : null; }
}
