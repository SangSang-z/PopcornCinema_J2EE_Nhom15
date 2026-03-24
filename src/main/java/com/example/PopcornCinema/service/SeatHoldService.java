package com.example.PopcornCinema.service;

import com.example.PopcornCinema.entity.*;
import com.example.PopcornCinema.repository.*;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SeatHoldService {

    private final SeatHoldRepository seatHoldRepo;

    public SeatHoldService(SeatHoldRepository seatHoldRepo){
        this.seatHoldRepo = seatHoldRepo;
    }

    /* ===============================
       GIỮ GHẾ 5 PHÚT
    =============================== */
    public boolean holdSeat(User user, Showtime showtime, Seat seat){

        boolean isHeld = seatHoldRepo.existsByShowtimeAndSeat(showtime, seat);

        if(isHeld){
            return false; // đã có người giữ
        }

        SeatHold hold = new SeatHold();
        hold.setUser(user);
        hold.setShowtime(showtime);
        hold.setSeat(seat);

        hold.setExpiresAt(LocalDateTime.now().plusMinutes(5)); // hold 5 phút

        seatHoldRepo.save(hold);

        return true;
    }
}