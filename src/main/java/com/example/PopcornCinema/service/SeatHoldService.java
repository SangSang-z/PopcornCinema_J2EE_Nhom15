package com.example.PopcornCinema.service;

import java.util.List;
import java.util.Set;

public interface SeatHoldService {
    void holdSeats(Long showtimeId, Long userId, List<Long> seatIds);
    void releaseSeats(Long showtimeId, Long userId, List<Long> seatIds);
    Set<Long> getHeldSeatIdsByOtherUsers(Long showtimeId, Long userId);
}
