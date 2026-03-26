package com.example.PopcornCinema.respository.projection;

import java.math.BigDecimal;

public interface SeatProjection {
    Long getSeatId();
    String getSeatRow();
    Integer getSeatNumber();
    String getSeatType();
    BigDecimal getExtraPrice();
}