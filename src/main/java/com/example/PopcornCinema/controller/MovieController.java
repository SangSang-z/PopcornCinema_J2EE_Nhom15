package com.example.PopcornCinema.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/movies")
public class MovieController {

    @GetMapping
    public String list(Model model){
        return "movies/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model){
        return "movies/detail";
    }

}