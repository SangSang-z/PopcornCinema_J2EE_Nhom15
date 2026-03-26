package com.example.PopcornCinema.dto;

import java.util.List;

public class ReleaseSeatsRequest {
    private Long userId;
    private List<Long> seatIds;

    public Long getUserId() {
        return userId;
    }

    public List<Long> getSeatIds() {
        return seatIds;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setSeatIds(List<Long> seatIds) {
        this.seatIds = seatIds;
    }
}