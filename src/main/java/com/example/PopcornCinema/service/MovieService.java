package com.example.PopcornCinema.service;

import com.example.PopcornCinema.dto.CinemaDto;
import com.example.PopcornCinema.dto.MovieDetailResponse;

import java.util.List;

public interface MovieService {
    MovieDetailResponse getMovieDetail(Long movieId);
    List<String> getAllCities();
    List<CinemaDto> getCinemasByCity(String city);
}