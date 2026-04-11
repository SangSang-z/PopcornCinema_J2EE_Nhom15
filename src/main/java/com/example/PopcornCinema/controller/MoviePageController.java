package com.example.PopcornCinema.controller;

import com.example.PopcornCinema.entity.Movie;
import com.example.PopcornCinema.repository.MovieRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/movies")
public class MoviePageController {

    private final MovieRepository movieRepository;

    public MoviePageController(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @GetMapping
    public String nowShowing(Model model) {
        List<Movie> nowShowing = movieRepository.findByStatusOrderByReleaseDateDescIdDesc("NOW_SHOWING");
        List<Movie> upcoming = movieRepository.findByStatusOrderByReleaseDateDescIdDesc("COMING_SOON");
        model.addAttribute("nowShowing", nowShowing);
        model.addAttribute("upcoming", upcoming);
        return "movie/index";
    }

    @GetMapping("/upcoming")
    public String upcoming() {
        return "movie/upcoming";
    }

    @GetMapping("/upcoming/{id}")
    public String upcomingDetail(@PathVariable Long id, Model model) {
        model.addAttribute("movieId", id);
        return "movie/upcoming-detail";
    }
}
