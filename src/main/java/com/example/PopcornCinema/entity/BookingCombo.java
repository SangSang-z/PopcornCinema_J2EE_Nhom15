package com.example.PopcornCinema.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "booking_combos",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_booking_combo_temp", columnNames = {"showtime_id", "user_id", "combo_id"}),
        @UniqueConstraint(name = "uq_booking_combo_booking", columnNames = {"booking_id", "combo_id"})
    }
)
public class BookingCombo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_id")
    private Long bookingId;

    @Column(name = "showtime_id")
    private Long showtimeId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "combo_id", nullable = false)
    private Long comboId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    public BookingCombo() {
    }

    public Long getId() { return id; }
    public Long getBookingId() { return bookingId; }
    public Long getShowtimeId() { return showtimeId; }
    public Long getUserId() { return userId; }
    public Long getComboId() { return comboId; }
    public Integer getQuantity() { return quantity; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setId(Long id) { this.id = id; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
    public void setShowtimeId(Long showtimeId) { this.showtimeId = showtimeId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setComboId(Long comboId) { this.comboId = comboId; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}