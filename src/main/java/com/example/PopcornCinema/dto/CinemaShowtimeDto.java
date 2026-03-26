package com.example.PopcornCinema.dto;

import java.util.ArrayList;
import java.util.List;

public class CinemaShowtimeDto {
    private Long cinemaId;
    private String cinemaName;
    private String city;
    private Long auditoriumId;
    private String auditoriumName;
    private List<ShowtimeDto> times = new ArrayList<>();

    public CinemaShowtimeDto() {
    }

    public CinemaShowtimeDto(Long cinemaId, String cinemaName, String city, Long auditoriumId, String auditoriumName) {
        this.cinemaId = cinemaId;
        this.cinemaName = cinemaName;
        this.city = city;
        this.auditoriumId = auditoriumId;
        this.auditoriumName = auditoriumName;
    }

    public Long getCinemaId() {
        return cinemaId;
    }

    public String getCinemaName() {
        return cinemaName;
    }

    public String getCity() {
        return city;
    }

    public Long getAuditoriumId() {
        return auditoriumId;
    }

    public String getAuditoriumName() {
        return auditoriumName;
    }

    public List<ShowtimeDto> getTimes() {
        return times;
    }
}
