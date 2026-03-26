package com.example.PopcornCinema.dto;

public class CinemaDto {
    private Long id;
    private String name;
    private String city;

    public CinemaDto() {
    }

    public CinemaDto(Long id, String name, String city) {
        this.id = id;
        this.name = name;
        this.city = city;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }
}