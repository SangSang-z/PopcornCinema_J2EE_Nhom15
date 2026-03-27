package com.example.PopcornCinema.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AdminBookingRowDto {
    private Long id;
    private String bookingCode;
    private String userName;
    private String userEmail;
    private String userPhone;
    private String movieTitle;
    private LocalDateTime showtimeStart;
    private String seatsText;
    private BigDecimal totalAmount;
    private String bookingStatus;
    private String paymentStatus;
    private String paymentMethod;
    private String transactionRef;
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public String getBookingCode() {
        return bookingCode;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public LocalDateTime getShowtimeStart() {
        return showtimeStart;
    }

    public String getSeatsText() {
        return seatsText;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getTransactionRef() {
        return transactionRef;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setBookingCode(String bookingCode) {
        this.bookingCode = bookingCode;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public void setShowtimeStart(LocalDateTime showtimeStart) {
        this.showtimeStart = showtimeStart;
    }

    public void setSeatsText(String seatsText) {
        this.seatsText = seatsText;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setTransactionRef(String transactionRef) {
        this.transactionRef = transactionRef;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}