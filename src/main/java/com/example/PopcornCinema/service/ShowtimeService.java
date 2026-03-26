package com.example.PopcornCinema.service;

import com.example.PopcornCinema.dto.SeatMapResponse;

public interface ShowtimeService {
    SeatMapResponse getSeatMap(Long showtimeId, Long userId);
}