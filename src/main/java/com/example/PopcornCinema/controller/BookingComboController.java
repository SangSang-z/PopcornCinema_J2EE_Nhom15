package com.example.PopcornCinema.controller;

import com.example.PopcornCinema.dto.SaveBookingCombosRequest;
import com.example.PopcornCinema.dto.SelectedComboDto;
import com.example.PopcornCinema.service.BookingComboService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/showtimes")
@CrossOrigin(origins = "*")
public class BookingComboController {

    private final BookingComboService bookingComboService;

    public BookingComboController(BookingComboService bookingComboService) {
        this.bookingComboService = bookingComboService;
    }

    @PostMapping("/{showtimeId}/booking-combos")
    public ResponseEntity<?> saveBookingCombos(
            @PathVariable Long showtimeId,
            @RequestBody SaveBookingCombosRequest request
    ) {
        bookingComboService.saveSelectedCombos(showtimeId, request);
        return ResponseEntity.ok(Map.of("message", "Lưu combo thành công"));
    }

    @GetMapping("/{showtimeId}/booking-combos")
    public ResponseEntity<List<SelectedComboDto>> getBookingCombos(
            @PathVariable Long showtimeId,
            @RequestParam("userId") Long userId
    ) {
        return ResponseEntity.ok(bookingComboService.getSelectedCombos(showtimeId, userId));
    }

    @DeleteMapping("/{showtimeId}/booking-combos")
    public ResponseEntity<?> clearBookingCombos(
            @PathVariable Long showtimeId,
            @RequestParam("userId") Long userId
    ) {
        bookingComboService.clearSelectedCombos(showtimeId, userId);
        return ResponseEntity.ok(Map.of("message", "Đã xóa combo tạm"));
    }
}