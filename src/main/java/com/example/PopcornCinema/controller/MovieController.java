package com.example.PopcornCinema.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MovieController {

    @GetMapping("/movies")
    public String showMoviePage() {
        return "movie/index";
    }

    @GetMapping("/movies/upcoming")
    public String showUpcomingMovies() {
        return "movie/upcoming";
    }

}