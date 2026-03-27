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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", insertable = false, updatable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "showtime_id", insertable = false, updatable = false)
    private Showtime showtime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", insertable = false, updatable = false)
    private Seat seat;

    public Ticket() {
    }

    public Long getId() { return id; }
    public Long getBookingId() { return bookingId; }
    public Long getShowtimeId() { return showtimeId; }
    public Long getSeatId() { return seatId; }
    public BigDecimal getTicketPrice() { return ticketPrice; }
    public BigDecimal getUnitPrice() { return ticketPrice; }
    public String getStatus() { return status; }
    public Booking getBooking() { return booking; }
    public Showtime getShowtime() { return showtime; }
    public Seat getSeat() { return seat; }

    public void setId(Long id) { this.id = id; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
    public void setShowtimeId(Long showtimeId) { this.showtimeId = showtimeId; }
    public void setSeatId(Long seatId) { this.seatId = seatId; }
    public void setTicketPrice(BigDecimal ticketPrice) { this.ticketPrice = ticketPrice; }
    public void setTicketPrice(double ticketPrice) { this.ticketPrice = BigDecimal.valueOf(ticketPrice); }
    public void setUnitPrice(double unitPrice) { this.ticketPrice = BigDecimal.valueOf(unitPrice); }
    public void setStatus(String status) { this.status = status; }
    public void setBooking(Booking booking) { this.booking = booking; this.bookingId = booking != null ? booking.getId() : null; }
    public void setShowtime(Showtime showtime) { this.showtime = showtime; this.showtimeId = showtime != null ? showtime.getId() : null; }
    public void setSeat(Seat seat) { this.seat = seat; this.seatId = seat != null ? seat.getId() : null; }
}
