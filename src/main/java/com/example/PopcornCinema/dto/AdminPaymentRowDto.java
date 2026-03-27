package com.example.PopcornCinema.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AdminPaymentRowDto {
    private Long id;
    private String orderCode;
    private String userName;
    private String userEmail;
    private String userPhone;
    private String movieTitle;
    private LocalDateTime showtimeStart;
    private String seatsText;
    private BigDecimal amount;
    private String transactionStatus;
    private String bookingCode;
    private String bookingStatus;
    private LocalDateTime createdAt;
    private boolean canConfirm;
    private boolean canReject;

    public Long getId() { return id; }
    public String getOrderCode() { return orderCode; }
    public String getUserName() { return userName; }
    public String getUserEmail() { return userEmail; }
    public String getUserPhone() { return userPhone; }
    public String getMovieTitle() { return movieTitle; }
    public LocalDateTime getShowtimeStart() { return showtimeStart; }
    public String getSeatsText() { return seatsText; }
    public BigDecimal getAmount() { return amount; }
    public String getTransactionStatus() { return transactionStatus; }
    public String getBookingCode() { return bookingCode; }
    public String getBookingStatus() { return bookingStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public boolean isCanConfirm() { return canConfirm; }
    public boolean isCanReject() { return canReject; }

    public void setId(Long id) { this.id = id; }
    public void setOrderCode(String orderCode) { this.orderCode = orderCode; }
    public void setUserName(String userName) { this.userName = userName; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public void setUserPhone(String userPhone) { this.userPhone = userPhone; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }
    public void setShowtimeStart(LocalDateTime showtimeStart) { this.showtimeStart = showtimeStart; }
    public void setSeatsText(String seatsText) { this.seatsText = seatsText; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setTransactionStatus(String transactionStatus) { this.transactionStatus = transactionStatus; }
    public void setBookingCode(String bookingCode) { this.bookingCode = bookingCode; }
    public void setBookingStatus(String bookingStatus) { this.bookingStatus = bookingStatus; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setCanConfirm(boolean canConfirm) { this.canConfirm = canConfirm; }
    public void setCanReject(boolean canReject) { this.canReject = canReject; }
}