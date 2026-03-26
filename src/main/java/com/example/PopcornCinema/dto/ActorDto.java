package com.example.PopcornCinema.dto;

public class ActorDto {
    private Long id;
    private String name;

    public ActorDto() {
    }

    public ActorDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
