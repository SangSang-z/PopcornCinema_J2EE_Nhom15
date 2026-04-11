package com.example.PopcornCinema.controller;

import com.example.PopcornCinema.entity.Movie;
import com.example.PopcornCinema.entity.Promotion;
import com.example.PopcornCinema.repository.MovieRepository;
import com.example.PopcornCinema.repository.PromotionRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/promotions")
public class PromotionPageController {

    private final PromotionRepository promotionRepository;
    private final MovieRepository movieRepository;

    public PromotionPageController(PromotionRepository promotionRepository,
                                   MovieRepository movieRepository) {
        this.promotionRepository = promotionRepository;
        this.movieRepository = movieRepository;
    }

    @GetMapping
    public String list(Model model) {
        List<Promotion> promotions = promotionRepository.findActivePromotions();
        List<Movie> upcomingMovies = movieRepository
                .findByStatusOrderByReleaseDateDescIdDesc("COMING_SOON");
        model.addAttribute("promotions", promotions);
        model.addAttribute("upcomingMovies", upcomingMovies);
        return "promotion/index";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, HttpSession session) {
        Promotion promotion = promotionRepository.findById(id).orElse(null);
        if (promotion == null) {
            return "redirect:/promotions";
        }
        Set<Long> savedPromotions = getSavedPromotions(session);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        if (promotion.getStartDate() != null) {
            model.addAttribute("startLabel", promotion.getStartDate().format(formatter));
        }
        if (promotion.getEndDate() != null) {
            model.addAttribute("endLabel", promotion.getEndDate().format(formatter));
        }
        model.addAttribute("savedAlready", savedPromotions.contains(id));
        model.addAttribute("promotion", promotion);
        return "promotion/detail";
    }

    @PostMapping("/save")
    public String save(@org.springframework.web.bind.annotation.RequestParam Long promotionId,
                       HttpSession session,
                       RedirectAttributes redirectAttributes) {
        Set<Long> savedPromotions = getSavedPromotions(session);
        if (savedPromotions.contains(promotionId)) {
            redirectAttributes.addFlashAttribute("saveError", "Bạn đã lưu mã này rồi.");
            return "redirect:/promotions/" + promotionId;
        }

        savedPromotions.add(promotionId);
        session.setAttribute("savedPromotions", savedPromotions);
        redirectAttributes.addFlashAttribute("saveSuccess", "Đã lưu mã khuyến mãi.");
        return "redirect:/promotions/" + promotionId;
    }

    @SuppressWarnings("unchecked")
    private Set<Long> getSavedPromotions(HttpSession session) {
        Object stored = session.getAttribute("savedPromotions");
        if (stored instanceof Set) {
            return (Set<Long>) stored;
        }
        Set<Long> fresh = new HashSet<>();
        session.setAttribute("savedPromotions", fresh);
        return fresh;
    }
}
