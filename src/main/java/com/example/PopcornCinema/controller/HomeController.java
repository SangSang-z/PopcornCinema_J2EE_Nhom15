package com.example.PopcornCinema.controller;

import com.example.PopcornCinema.repository.MovieRepository;
import com.example.PopcornCinema.repository.PromotionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private PromotionRepository promotionRepository;

    @GetMapping("/")
    public String home(Model model){
        var nowShowing = movieRepository
                .findByStatusOrderByReleaseDateDescIdDesc("NOW_SHOWING");
        var upcoming = movieRepository
                .findByStatusOrderByReleaseDateDescIdDesc("COMING_SOON");
        var promotions = promotionRepository.findActivePromotions();

        model.addAttribute("nowShowing", nowShowing);
        model.addAttribute("upcoming", upcoming);
        model.addAttribute("promotions", promotions);

        return "index";
    }
}
