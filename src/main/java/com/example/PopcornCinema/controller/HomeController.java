package com.example.PopcornCinema.controller;

import com.example.PopcornCinema.entity.Movie;
import com.example.PopcornCinema.repository.MovieRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private MovieRepository movieRepository;

    @GetMapping("/")
    public String home(Model model){

        List<Movie> movies = movieRepository.findAll();
        model.addAttribute("movies", movies);

        return "index";
    }
}