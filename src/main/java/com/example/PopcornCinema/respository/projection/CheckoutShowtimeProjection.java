package com.example.PopcornCinema.respository.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface CheckoutShowtimeProjection {
    Long getShowtimeId();
    String getMovieTitle();
    String getPosterUrl();
    String getAgeRating();
    String getCinemaName();
    String getAuditoriumName();
    LocalDateTime getStartTime();
    LocalDateTime getEndTime();
    BigDecimal getBasePrice();
}