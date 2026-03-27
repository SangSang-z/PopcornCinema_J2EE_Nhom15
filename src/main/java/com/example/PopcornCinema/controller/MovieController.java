package com.example.PopcornCinema.controller;

import com.example.PopcornCinema.dto.CinemaDto;
import com.example.PopcornCinema.dto.MovieDetailResponse;
import com.example.PopcornCinema.service.MovieService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@CrossOrigin(origins = "*")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<MovieDetailResponse> getMovieDetail(@PathVariable("id") Long id) {
        return ResponseEntity.ok(movieService.getMovieDetail(id));
    }

    @GetMapping("/cities")
    public ResponseEntity<List<String>> getCities() {
        return ResponseEntity.ok(movieService.getAllCities());
    }

    @GetMapping("/cinemas")
    public ResponseEntity<List<CinemaDto>> getCinemasByCity(
            @RequestParam(value = "city", required = false) String city
    ) {
        return ResponseEntity.ok(movieService.getCinemasByCity(city));
    }
}
