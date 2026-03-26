package com.example.PopcornCinema.dto;

public class RelatedMovieDto {
    private Long id;
    private String title;
    private String posterUrl;
    private String ageRating;

    public RelatedMovieDto() {
    }

    public RelatedMovieDto(Long id, String title, String posterUrl, String ageRating) {
        this.id = id;
        this.title = title;
        this.posterUrl = posterUrl;
        this.ageRating = ageRating;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public String getAgeRating() {
        return ageRating;
    }
}