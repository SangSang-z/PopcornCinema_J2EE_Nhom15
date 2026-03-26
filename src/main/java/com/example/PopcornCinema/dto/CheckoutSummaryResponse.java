package com.example.PopcornCinema.dto;

import java.math.BigDecimal;

public class CheckoutSummaryResponse {
    private String movieTitle;
    private String posterUrl;
    private String ageRating;
    private String cinemaName;
    private String auditoriumName;
    private String startTimeText;
    private String seatsText;
    private BigDecimal seatTotal;
    private BigDecimal comboTotal;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;

    public String getMovieTitle() { return movieTitle; }
    public String getPosterUrl() { return posterUrl; }
    public String getAgeRating() { return ageRating; }
    public String getCinemaName() { return cinemaName; }
    public String getAuditoriumName() { return auditoriumName; }
    public String getStartTimeText() { return startTimeText; }
    public String getSeatsText() { return seatsText; }
    public BigDecimal getSeatTotal() { return seatTotal; }
    public BigDecimal getComboTotal() { return comboTotal; }
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public BigDecimal getTotalAmount() { return totalAmount; }

    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }
    public void setAgeRating(String ageRating) { this.ageRating = ageRating; }
    public void setCinemaName(String cinemaName) { this.cinemaName = cinemaName; }
    public void setAuditoriumName(String auditoriumName) { this.auditoriumName = auditoriumName; }
    public void setStartTimeText(String startTimeText) { this.startTimeText = startTimeText; }
    public void setSeatsText(String seatsText) { this.seatsText = seatsText; }
    public void setSeatTotal(BigDecimal seatTotal) { this.seatTotal = seatTotal; }
    public void setComboTotal(BigDecimal comboTotal) { this.comboTotal = comboTotal; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
}