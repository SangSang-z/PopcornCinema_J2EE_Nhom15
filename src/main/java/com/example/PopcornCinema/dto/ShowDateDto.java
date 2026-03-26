package com.example.PopcornCinema.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ShowDateDto {
    private LocalDate date;
    private List<CinemaShowtimeDto> cinemas = new ArrayList<>();

    public ShowDateDto() {
    }

    public ShowDateDto(LocalDate date) {
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }

    public List<CinemaShowtimeDto> getCinemas() {
        return cinemas;
    }
}
