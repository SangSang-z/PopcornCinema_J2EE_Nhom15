package com.example.PopcornCinema.repository;

import com.example.PopcornCinema.entity.Movie;
import com.example.PopcornCinema.repository.projection.ActorProjection;
import com.example.PopcornCinema.repository.projection.CinemaProjection;
import com.example.PopcornCinema.repository.projection.GenreProjection;
import com.example.PopcornCinema.repository.projection.RelatedMovieProjection;
import com.example.PopcornCinema.repository.projection.ShowtimeSeatMapProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Query(value = """
        SELECT g.id AS id, g.name AS name
        FROM movie_genres mg
        JOIN genres g ON mg.genre_id = g.id
        WHERE mg.movie_id = :movieId
        ORDER BY g.name
        """, nativeQuery = true)
    List<GenreProjection> findGenresByMovieId(@Param("movieId") Long movieId);

    @Query(value = """
        SELECT a.id AS id, a.name AS name
        FROM movie_actors ma
        JOIN actors a ON ma.actor_id = a.id
        WHERE ma.movie_id = :movieId
        ORDER BY a.name
        """, nativeQuery = true)
    List<ActorProjection> findActorsByMovieId(@Param("movieId") Long movieId);

    @Query(value = """
        SELECT
            s.id AS showtimeId,
            s.start_time AS startTime,
            s.end_time AS endTime,
            s.base_price AS basePrice,
            c.id AS cinemaId,
            c.name AS cinemaName,
            c.city AS city,
            au.id AS auditoriumId,
            au.name AS auditoriumName
        FROM showtimes s
        JOIN auditoriums au ON s.auditorium_id = au.id
        JOIN cinemas c ON au.cinema_id = c.id
        WHERE s.movie_id = :movieId
          AND s.status IN ('OPEN', 'SCHEDULED')
          AND s.start_time >= NOW()
        ORDER BY s.start_time ASC
        """, nativeQuery = true)
    List<ShowtimeSeatMapProjection> findOpenShowtimesByMovieId(@Param("movieId") Long movieId);

    @Query(value = """
        SELECT
            m.id AS id,
            m.title AS title,
            m.poster_url AS posterUrl,
            m.age_rating AS ageRating
        FROM movies m
        WHERE m.status = 'NOW_SHOWING'
          AND m.id <> :movieId
        ORDER BY m.release_date DESC, m.id DESC
        LIMIT 4
        """, nativeQuery = true)
    List<RelatedMovieProjection> findRelatedMovies(@Param("movieId") Long movieId);

    @Query(value = """
        SELECT DISTINCT c.city AS city
        FROM cinemas c
        WHERE c.status = 'ACTIVE'
        ORDER BY c.city
        """, nativeQuery = true)
    List<String> findAllActiveCities();

    @Query(value = """
        SELECT c.id AS id, c.name AS name, c.city AS city
        FROM cinemas c
        WHERE c.status = 'ACTIVE'
          AND (:city IS NULL OR :city = '' OR c.city = :city)
        ORDER BY c.city, c.name
        """, nativeQuery = true)
    List<CinemaProjection> findCinemasByCity(@Param("city") String city);
}