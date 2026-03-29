package com.example.PopcornCinema.controller;

import com.example.PopcornCinema.entity.Promotion;
import com.example.PopcornCinema.respository.PromotionRepository;
import com.example.PopcornCinema.service.PromotionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class PromotionController {

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private PromotionService promotionService;

    // ✅ Trang danh sách
    @GetMapping("/promotions")
    public String showPromotionPage(Model model) {

        List<Promotion> list = promotionRepository.findAll();

        model.addAttribute("promotions", list);

        return "promotion/index";
    }

    @GetMapping("/promotions/{id}")
    public String getPromotionDetail(@PathVariable Long id, Model model) {

        Promotion promotion = promotionRepository.findById(id).orElse(null);

        model.addAttribute("promotion", promotion);

        return "promotion/detail";
    }

    @PostMapping("/save")
    public String savePromotion(
            @RequestParam String userId,
            @RequestParam Long promotionId
    ) {
        promotionService.savePromotion(userId, promotionId);

        return "redirect:/promotions/" + promotionId;
    }
}