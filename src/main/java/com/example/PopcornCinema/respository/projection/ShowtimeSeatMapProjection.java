package com.example.PopcornCinema.respository.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface ShowtimeSeatMapProjection {
    Long getShowtimeId();
    String getMovieTitle();
    String getPosterUrl();
    String getAgeRating();

    Long getCinemaId();
    String getCinemaName();
    String getCity();

    Long getAuditoriumId();
    String getAuditoriumName();

    LocalDateTime getStartTime();
    LocalDateTime getEndTime();
    BigDecimal getBasePrice();
}