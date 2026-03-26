package com.example.PopcornCinema.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_id", nullable = false)
    private Long bookingId;

    @Column(name = "showtime_id", nullable = false)
    private Long showtimeId;

    @Column(name = "seat_id", nullable = false)
    private Long seatId;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal ticketPrice;

    @Column(name = "status", nullable = false)
    private String status;

    public Ticket() {
    }

    public Long getId() { return id; }
    public Long getBookingId() { return bookingId; }
    public Long getShowtimeId() { return showtimeId; }
    public Long getSeatId() { return seatId; }
    public BigDecimal getTicketPrice() { return ticketPrice; }
    public String getStatus() { return status; }

    public void setId(Long id) { this.id = id; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
    public void setShowtimeId(Long showtimeId) { this.showtimeId = showtimeId; }
    public void setSeatId(Long seatId) { this.seatId = seatId; }
    public void setTicketPrice(BigDecimal ticketPrice) { this.ticketPrice = ticketPrice; }
    public void setStatus(String status) { this.status = status; }
}