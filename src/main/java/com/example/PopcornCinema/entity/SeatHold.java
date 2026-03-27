package com.example.PopcornCinema.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "seat_holds")
public class SeatHold {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "showtime_id", nullable = false)
    private Long showtimeId;

    @Column(name = "seat_id", nullable = false)
    private Long seatId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "showtime_id", insertable = false, updatable = false)
    private Showtime showtime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", insertable = false, updatable = false)
    private Seat seat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    public SeatHold() {
    }

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Long getShowtimeId() { return showtimeId; }
    public Long getSeatId() { return seatId; }
    public Long getUserId() { return userId; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Showtime getShowtime() { return showtime; }
    public Seat getSeat() { return seat; }
    public User getUser() { return user; }

    public void setId(Long id) { this.id = id; }
    public void setShowtimeId(Long showtimeId) { this.showtimeId = showtimeId; }
    public void setSeatId(Long seatId) { this.seatId = seatId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setShowtime(Showtime showtime) { this.showtime = showtime; this.showtimeId = showtime != null ? showtime.getId() : null; }
    public void setSeat(Seat seat) { this.seat = seat; this.seatId = seat != null ? seat.getId() : null; }
    public void setUser(User user) { this.user = user; this.userId = user != null ? user.getId() : null; }
}
