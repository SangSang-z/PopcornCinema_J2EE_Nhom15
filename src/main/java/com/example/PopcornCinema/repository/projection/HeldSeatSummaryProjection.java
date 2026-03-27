package com.example.PopcornCinema.repository.projection;

import java.math.BigDecimal;

public interface HeldSeatSummaryProjection {
    Long getSeatId();
    String getSeatRow();
    Integer getSeatNumber();
    BigDecimal getExtraPrice();
}
