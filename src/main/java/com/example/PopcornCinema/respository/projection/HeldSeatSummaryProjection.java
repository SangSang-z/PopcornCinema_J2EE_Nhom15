package com.example.PopcornCinema.respository.projection;

import java.math.BigDecimal;

public interface HeldSeatSummaryProjection {
    Long getSeatId();
    String getSeatRow();
    Integer getSeatNumber();
    BigDecimal getExtraPrice();
}