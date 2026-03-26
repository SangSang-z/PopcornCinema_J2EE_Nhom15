package com.example.PopcornCinema.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ShowtimeDto {
    private Long showtimeId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal basePrice;

    public ShowtimeDto() {
    }

    public ShowtimeDto(Long showtimeId, LocalDateTime startTime, LocalDateTime endTime, BigDecimal basePrice) {
        this.showtimeId = showtimeId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.basePrice = basePrice;
    }

    public Long getShowtimeId() {
        return showtimeId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }
}
