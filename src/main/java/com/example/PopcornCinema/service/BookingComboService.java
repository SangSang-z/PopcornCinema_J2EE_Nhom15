package com.example.PopcornCinema.service;

import com.example.PopcornCinema.dto.SaveBookingCombosRequest;
import com.example.PopcornCinema.dto.SelectedComboDto;

import java.util.List;

public interface BookingComboService {
    void saveSelectedCombos(Long showtimeId, SaveBookingCombosRequest request);
    List<SelectedComboDto> getSelectedCombos(Long showtimeId, Long userId);
    void clearSelectedCombos(Long showtimeId, Long userId);

    void moveTempCombosToBooking(Long showtimeId, Long userId, Long bookingId);
    List<SelectedComboDto> getBookedCombos(Long bookingId);
}