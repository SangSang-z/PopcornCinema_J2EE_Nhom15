package com.example.PopcornCinema.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "showtimes")
public class Showtime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "movie_id", nullable = false)
    private Long movieId;

    @Column(name = "auditorium_id", nullable = false)
    private Long auditoriumId;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "base_price", nullable = false)
    private BigDecimal basePrice;

    @Column(nullable = false)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", insertable = false, updatable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auditorium_id", insertable = false, updatable = false)
    private Auditorium auditorium;

    public Showtime() {
    }

    public Long getId() { return id; }
    public Long getMovieId() { return movieId; }
    public Long getAuditoriumId() { return auditoriumId; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public BigDecimal getBasePrice() { return basePrice; }
    public String getStatus() { return status; }
    public Movie getMovie() { return movie; }
    public Auditorium getAuditorium() { return auditorium; }

    public void setId(Long id) { this.id = id; }
    public void setMovieId(Long movieId) { this.movieId = movieId; }
    public void setAuditoriumId(Long auditoriumId) { this.auditoriumId = auditoriumId; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }
    public void setBasePrice(Double basePrice) { this.basePrice = basePrice == null ? null : BigDecimal.valueOf(basePrice); }
    public void setStatus(String status) { this.status = status; }
    public void setMovie(Movie movie) { this.movie = movie; this.movieId = movie != null ? movie.getId() : null; }
    public void setAuditorium(Auditorium auditorium) { this.auditorium = auditorium; this.auditoriumId = auditorium != null ? auditorium.getId() : null; }
}
