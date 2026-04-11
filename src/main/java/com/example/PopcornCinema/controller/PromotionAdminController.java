package com.example.PopcornCinema.controller;

import com.example.PopcornCinema.entity.Promotion;
import com.example.PopcornCinema.repository.PromotionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

@Controller
@RequestMapping("/admin/promotions")
public class PromotionAdminController {

    private final PromotionRepository promotionRepo;
    private static final String UPLOAD_DIR = "uploads/promotions/";

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
        model.addAttribute("activePage","promotions");
        return "admin/promotions/create";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Promotion promotion,
                       @RequestParam(value = "posterFile", required = false) MultipartFile posterFile,
                       Model model){
        try {
            if (promotion.getCode() == null || promotion.getCode().isBlank()) {
                model.addAttribute("error", "Mã khuyến mãi không được để trống");
                model.addAttribute("promotion", promotion);
                model.addAttribute("activePage","promotions");
                return "admin/promotions/create";
            }

            if (promotion.getTitle() == null || promotion.getTitle().isBlank()) {
                promotion.setTitle(promotion.getCode());
            }

            if (promotion.getStatus() == null || promotion.getStatus().isBlank()) {
                promotion.setStatus("ACTIVE");
            }

            if (promotion.getStartDate() == null) {
                promotion.setStartDate(LocalDate.now().atStartOfDay());
            }

            if (promotion.getEndDate() == null) {
                promotion.setEndDate(LocalDate.now().plusMonths(1).atTime(23, 59, 59));
            }

            if (promotion.getMinOrderValue() == null) {
                promotion.setMinOrderValue(0);
            }

            if (posterFile != null && !posterFile.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + posterFile.getOriginalFilename();
                Path uploadPath = Paths.get(UPLOAD_DIR);
                Files.createDirectories(uploadPath);
                Files.write(uploadPath.resolve(fileName), posterFile.getBytes());
                promotion.setPosterUrl("/uploads/promotions/" + fileName);
            }

            promotionRepo.save(promotion);
            return "redirect:/admin/promotions";

        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            model.addAttribute("promotion", promotion);
            model.addAttribute("activePage","promotions");
            return "admin/promotions/create";
        }
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
