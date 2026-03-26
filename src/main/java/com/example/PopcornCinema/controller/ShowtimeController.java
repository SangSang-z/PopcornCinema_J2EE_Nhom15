package com.example.PopcornCinema.controller;

import com.example.PopcornCinema.dto.SeatMapResponse;
import com.example.PopcornCinema.service.ShowtimeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/showtimes")
@CrossOrigin(origins = "*")
public class ShowtimeController {

    private final ShowtimeService showtimeService;

    public ShowtimeController(ShowtimeService showtimeService) {
        this.showtimeService = showtimeService;
    }

    @GetMapping("/{id}/seat-map")
    public ResponseEntity<SeatMapResponse> getSeatMap(
            @PathVariable("id") Long id,
            @RequestParam("userId") Long userId
    ) {
        return ResponseEntity.ok(showtimeService.getSeatMap(id, userId));
    }
}