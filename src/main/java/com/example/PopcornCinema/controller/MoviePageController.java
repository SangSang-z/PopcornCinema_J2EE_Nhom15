package com.example.PopcornCinema.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MoviePageController {

    @GetMapping("/movie-detail")
    public String movieDetailPage() {
        return "booking/movie-detail";
    }
}