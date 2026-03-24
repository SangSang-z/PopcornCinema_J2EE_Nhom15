package com.example.PopcornCinema.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bookingCode;

    /* ===============================
       RELATIONSHIP
    =============================== */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "showtime_id")
    private Showtime showtime;

    /* ===============================
       BOOKING INFO
    =============================== */
    @Column(name = "total_amount")
    private double totalAmount;

    private String status; // PENDING_PAYMENT, PAID, EXPIRED

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Booking() {}

    /* ===============================
       AUTO SET
    =============================== */
    @PrePersist
    public void prePersist(){
        this.createdAt = LocalDateTime.now();

        if(this.status == null){
            this.status = "PENDING_PAYMENT";
        }
    }

    @PreUpdate
    public void preUpdate(){
        this.updatedAt = LocalDateTime.now();
    }

    /* ===============================
       GETTER SETTER
    =============================== */

    public Long getId() { return id; }

    public String getBookingCode() { return bookingCode; }
    public void setBookingCode(String bookingCode) { this.bookingCode = bookingCode; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Showtime getShowtime() { return showtime; }
    public void setShowtime(Showtime showtime) { this.showtime = showtime; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}