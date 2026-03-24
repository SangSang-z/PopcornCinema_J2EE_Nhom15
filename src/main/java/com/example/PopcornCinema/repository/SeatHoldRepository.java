package com.example.PopcornCinema.repository;

import com.example.PopcornCinema.entity.Seat;
import com.example.PopcornCinema.entity.SeatHold;
import com.example.PopcornCinema.entity.Showtime;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SeatHoldRepository extends JpaRepository<SeatHold, Long> {

    // check ghế đã bị giữ chưa
    boolean existsByShowtimeAndSeat(Showtime showtime, Seat seat);

    // lấy hold hết hạn
    List<SeatHold> findByExpiresAtBefore(LocalDateTime time);

    // lấy hold theo user
    List<SeatHold> findByUser_Id(Long userId);
}