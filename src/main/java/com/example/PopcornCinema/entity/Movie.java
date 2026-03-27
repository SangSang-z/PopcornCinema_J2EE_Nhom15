package com.example.PopcornCinema.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "movies")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "poster_url")
    private String posterUrl;

    @Column(name = "banner_url")
    private String bannerUrl;

    @Column(nullable = false, length = 150)
    private String director;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(length = 50)
    private String language;

    @Column(name = "age_rating", length = 10)
    private String ageRating;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @Column(columnDefinition = "TEXT")
    private String actors;

    @Column(length = 255)
    private String genres;

    @Column(length = 100)
    private String subtitle;

    @Column(name = "trailer_url", length = 255)
    private String trailerUrl;



    public Movie() {
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getPosterUrl() { return posterUrl; }
    public String getBannerUrl() { return bannerUrl; }
    public String getDirector() { return director; }
    public String getDescription() { return description; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public LocalDate getReleaseDate() { return releaseDate; }
    public String getLanguage() { return language; }
    public String getAgeRating() { return ageRating; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public String getActors() { return actors; }
    public String getGenres() { return genres; }
    public String getSubtitle() { return subtitle; }
    public String getTrailerUrl() { return trailerUrl; }
    

    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }
    public void setBannerUrl(String bannerUrl) { this.bannerUrl = bannerUrl; }
    public void setDirector(String director) { this.director = director; }
    public void setDescription(String description) { this.description = description; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
    public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate; }
    public void setLanguage(String language) { this.language = language; }
    public void setAgeRating(String ageRating) { this.ageRating = ageRating; }
    public void setStatus(String status) { this.status = status; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void setActors(String actors) { this.actors = actors; }
    public void setGenres(String genres) { this.genres = genres; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }
    public void setTrailerUrl(String trailerUrl) { this.trailerUrl = trailerUrl; }
    
}
