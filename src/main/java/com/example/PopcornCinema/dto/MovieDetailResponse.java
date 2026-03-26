package com.example.PopcornCinema.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MovieDetailResponse {
    private Long id;
    private String title;
    private String posterUrl;
    private String director;
    private String description;
    private Integer durationMinutes;
    private LocalDate releaseDate;
    private String language;
    private String ageRating;
    private String status;

    private List<GenreDto> genres = new ArrayList<>();
    private List<ActorDto> actors = new ArrayList<>();
    private List<ShowDateDto> showDates = new ArrayList<>();
    private List<RelatedMovieDto> relatedMovies = new ArrayList<>();

    public MovieDetailResponse() {
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

    public String getDirector() {
        return director;
    }

    public String getDescription() {
        return description;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public String getLanguage() {
        return language;
    }

    public String getAgeRating() {
        return ageRating;
    }

    public String getStatus() {
        return status;
    }

    public List<GenreDto> getGenres() {
        return genres;
    }

    public List<ActorDto> getActors() {
        return actors;
    }

    public List<ShowDateDto> getShowDates() {
        return showDates;
    }

    public List<RelatedMovieDto> getRelatedMovies() {
        return relatedMovies;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setAgeRating(String ageRating) {
        this.ageRating = ageRating;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setGenres(List<GenreDto> genres) {
        this.genres = genres;
    }

    public void setActors(List<ActorDto> actors) {
        this.actors = actors;
    }

    public void setShowDates(List<ShowDateDto> showDates) {
        this.showDates = showDates;
    }

    public void setRelatedMovies(List<RelatedMovieDto> relatedMovies) {
        this.relatedMovies = relatedMovies;
    }
}