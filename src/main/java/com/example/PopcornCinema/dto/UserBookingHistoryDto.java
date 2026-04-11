package com.example.PopcornCinema.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class UserBookingHistoryDto {
    private String bookingCode;
    private String movieTitle;
    private String cinemaName;
    private String auditoriumName;
    private LocalDateTime showtimeStart;
    private String seatsText;
    private BigDecimal totalAmount;
    private String bookingStatus;
    private String paymentStatus;
    private String paymentMethod;
    private LocalDateTime createdAt;

    public String getBookingCode() { return bookingCode; }
    public String getMovieTitle() { return movieTitle; }
    public String getCinemaName() { return cinemaName; }
    public String getAuditoriumName() { return auditoriumName; }
    public LocalDateTime getShowtimeStart() { return showtimeStart; }
    public String getSeatsText() { return seatsText; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public String getBookingStatus() { return bookingStatus; }
    public String getPaymentStatus() { return paymentStatus; }
    public String getPaymentMethod() { return paymentMethod; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setBookingCode(String bookingCode) { this.bookingCode = bookingCode; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }
    public void setCinemaName(String cinemaName) { this.cinemaName = cinemaName; }
    public void setAuditoriumName(String auditoriumName) { this.auditoriumName = auditoriumName; }
    public void setShowtimeStart(LocalDateTime showtimeStart) { this.showtimeStart = showtimeStart; }
    public void setSeatsText(String seatsText) { this.seatsText = seatsText; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public void setBookingStatus(String bookingStatus) { this.bookingStatus = bookingStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
