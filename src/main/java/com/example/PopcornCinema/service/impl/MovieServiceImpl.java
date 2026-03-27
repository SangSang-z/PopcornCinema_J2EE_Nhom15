package com.example.PopcornCinema.service.impl;

import com.example.PopcornCinema.dto.*;
import com.example.PopcornCinema.entity.Movie;
import com.example.PopcornCinema.repository.MovieRepository;
// import com.example.PopcornCinema.repository.projection.ActorProjection;
// import com.example.PopcornCinema.repository.projection.GenreProjection;
// import com.example.PopcornCinema.repository.projection.RelatedMovieProjection;
import com.example.PopcornCinema.repository.projection.ShowtimeSeatMapProjection;
import com.example.PopcornCinema.service.MovieService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;

    public MovieServiceImpl(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    public MovieDetailResponse getMovieDetail(Long movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phim với id = " + movieId));

        MovieDetailResponse response = new MovieDetailResponse();
        response.setId(movie.getId());
        response.setTitle(movie.getTitle());
        response.setPosterUrl(movie.getPosterUrl());
        response.setBannerUrl(movie.getBannerUrl());
        response.setDirector(movie.getDirector());
        response.setDescription(movie.getDescription());
        response.setDurationMinutes(movie.getDurationMinutes());
        response.setReleaseDate(movie.getReleaseDate());
        response.setLanguage(movie.getLanguage());
        response.setAgeRating(movie.getAgeRating());
        response.setStatus(movie.getStatus());

        List<GenreDto> genres = movieRepository.findGenresByMovieId(movieId)
                .stream()
                .map(g -> new GenreDto(g.getId(), g.getName()))
                .toList();
        response.setGenres(genres);

        List<ActorDto> actors = movieRepository.findActorsByMovieId(movieId)
                .stream()
                .map(a -> new ActorDto(a.getId(), a.getName()))
                .toList();
        response.setActors(actors);

        response.setShowDates(mapShowDates(movieRepository.findOpenShowtimesByMovieId(movieId)));

        List<RelatedMovieDto> relatedMovies = movieRepository.findRelatedMovies(movieId)
                .stream()
                .map(r -> new RelatedMovieDto(
                        r.getId(),
                        r.getTitle(),
                        r.getPosterUrl(),
                        r.getAgeRating()
                ))
                .toList();
        response.setRelatedMovies(relatedMovies);

        return response;
    }

    private List<ShowDateDto> mapShowDates(List<ShowtimeSeatMapProjection> rows) {
        Map<LocalDate, Map<String, CinemaShowtimeDto>> dateCinemaMap = new LinkedHashMap<>();

        for (ShowtimeSeatMapProjection row : rows) {
            LocalDate showDate = row.getStartTime().toLocalDate();

            dateCinemaMap.putIfAbsent(showDate, new LinkedHashMap<>());
            Map<String, CinemaShowtimeDto> cinemaMap = dateCinemaMap.get(showDate);

            String cinemaKey = row.getCinemaId() + "_" + row.getAuditoriumId();

            cinemaMap.putIfAbsent(cinemaKey, new CinemaShowtimeDto(
                    row.getCinemaId(),
                    row.getCinemaName(),
                    row.getCity(),
                    row.getAuditoriumId(),
                    row.getAuditoriumName()
            ));

            cinemaMap.get(cinemaKey).getTimes().add(
                    new ShowtimeDto(
                            row.getShowtimeId(),
                            row.getStartTime(),
                            row.getEndTime(),
                            row.getBasePrice()
                    )
            );
        }

        List<ShowDateDto> result = new ArrayList<>();

        for (Map.Entry<LocalDate, Map<String, CinemaShowtimeDto>> entry : dateCinemaMap.entrySet()) {
            ShowDateDto showDateDto = new ShowDateDto(entry.getKey());
            showDateDto.getCinemas().addAll(entry.getValue().values());
            result.add(showDateDto);
        }

        return result;
    }
    @Override
        public List<String> getAllCities() {
        return movieRepository.findAllActiveCities();
        }

        @Override
        public List<CinemaDto> getCinemasByCity(String city) {
        return movieRepository.findCinemasByCity(city)
                .stream()
                .map(c -> new CinemaDto(c.getId(), c.getName(), c.getCity()))
                .toList();
        }
}
