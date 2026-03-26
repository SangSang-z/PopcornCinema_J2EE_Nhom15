package com.example.PopcornCinema.dto;

import java.util.List;

public class SaveBookingCombosRequest {

    private Long userId;
    private List<BookingComboItemDto> items;

    public Long getUserId() { return userId; }
    public List<BookingComboItemDto> getItems() { return items; }

    public void setUserId(Long userId) { this.userId = userId; }
    public void setItems(List<BookingComboItemDto> items) { this.items = items; }
}