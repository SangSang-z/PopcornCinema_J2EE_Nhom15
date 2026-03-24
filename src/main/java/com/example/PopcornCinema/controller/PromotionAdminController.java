package com.example.PopcornCinema.controller;

import com.example.PopcornCinema.entity.Promotion;
import com.example.PopcornCinema.repository.PromotionRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/promotions")
public class PromotionAdminController {

    private final PromotionRepository promotionRepo;

    public PromotionAdminController(PromotionRepository promotionRepo){
        this.promotionRepo = promotionRepo;
    }

    @GetMapping
    public String list(Model model){

        model.addAttribute("promotions", promotionRepo.findAll());
        model.addAttribute("activePage","promotions");

        return "admin/promotions/list";
    }

    @GetMapping("/create")
    public String createForm(Model model){

        model.addAttribute("promotion", new Promotion());

        return "admin/promotions/create";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Promotion promotion){

        promotionRepo.save(promotion);

        return "redirect:/admin/promotions";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id){

        promotionRepo.deleteById(id);

        return "redirect:/admin/promotions";
    }

    @GetMapping("/toggle/{id}")
    public String toggle(@PathVariable Long id){

        Promotion promo = promotionRepo.findById(id).orElse(null);

        if(promo != null){

            promo.setActive(!promo.isActive());

            promotionRepo.save(promo);
        }

        return "redirect:/admin/promotions";
    }
}