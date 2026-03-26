package com.example.PopcornCinema.controller;

import com.example.PopcornCinema.dto.HoldSeatsRequest;
import com.example.PopcornCinema.dto.ReleaseSeatsRequest;
import com.example.PopcornCinema.service.SeatHoldService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/showtimes")
@CrossOrigin(origins = "*")
public class SeatHoldController {

    private final SeatHoldService seatHoldService;

    public SeatHoldController(SeatHoldService seatHoldService) {
        this.seatHoldService = seatHoldService;
    }

    @PostMapping("/{showtimeId}/hold-seats")
    public ResponseEntity<?> holdSeats(
            @PathVariable Long showtimeId,
            @RequestBody HoldSeatsRequest request
    ) {
        seatHoldService.holdSeats(showtimeId, request.getUserId(), request.getSeatIds());
        return ResponseEntity.ok(Map.of("message", "Giữ ghế thành công"));
    }

    @DeleteMapping("/{showtimeId}/hold-seats")
    public ResponseEntity<?> releaseSeats(
            @PathVariable Long showtimeId,
            @RequestBody ReleaseSeatsRequest request
    ) {
        seatHoldService.releaseSeats(showtimeId, request.getUserId(), request.getSeatIds());
        return ResponseEntity.ok(Map.of("message", "Bỏ giữ ghế thành công"));
    }
}