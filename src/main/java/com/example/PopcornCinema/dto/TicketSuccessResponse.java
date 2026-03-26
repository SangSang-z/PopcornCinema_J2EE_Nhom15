package com.example.PopcornCinema.dto;

import java.math.BigDecimal;

public class TicketSuccessResponse {
    private String bookingCode;
    private String movieTitle;
    private String posterUrl;
    private String ageRating;
    private String cinemaName;
    private String auditoriumName;
    private String showtimeText;
    private String seatsText;
    private String comboText;
    private BigDecimal seatTotal;
    private BigDecimal comboTotal;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private String paymentStatus;

    public String getBookingCode() { return bookingCode; }
    public String getMovieTitle() { return movieTitle; }
    public String getPosterUrl() { return posterUrl; }
    public String getAgeRating() { return ageRating; }
    public String getCinemaName() { return cinemaName; }
    public String getAuditoriumName() { return auditoriumName; }
    public String getShowtimeText() { return showtimeText; }
    public String getSeatsText() { return seatsText; }
    public String getComboText() { return comboText; }
    public BigDecimal getSeatTotal() { return seatTotal; }
    public BigDecimal getComboTotal() { return comboTotal; }
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public String getPaymentStatus() { return paymentStatus; }

    public void setBookingCode(String bookingCode) { this.bookingCode = bookingCode; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }
    public void setAgeRating(String ageRating) { this.ageRating = ageRating; }
    public void setCinemaName(String cinemaName) { this.cinemaName = cinemaName; }
    public void setAuditoriumName(String auditoriumName) { this.auditoriumName = auditoriumName; }
    public void setShowtimeText(String showtimeText) { this.showtimeText = showtimeText; }
    public void setSeatsText(String seatsText) { this.seatsText = seatsText; }
    public void setComboText(String comboText) { this.comboText = comboText; }
    public void setSeatTotal(BigDecimal seatTotal) { this.seatTotal = seatTotal; }
    public void setComboTotal(BigDecimal comboTotal) { this.comboTotal = comboTotal; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
}