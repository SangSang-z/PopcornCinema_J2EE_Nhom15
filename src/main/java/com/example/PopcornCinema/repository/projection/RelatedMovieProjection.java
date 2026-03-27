package com.example.PopcornCinema.repository.projection;

public interface RelatedMovieProjection {
    Long getId();
    String getTitle();
    String getPosterUrl();
    String getAgeRating();
}