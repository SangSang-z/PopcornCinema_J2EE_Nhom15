package com.example.PopcornCinema.controller;

import com.example.PopcornCinema.dto.ComboDto;
import com.example.PopcornCinema.entity.Combo;
import com.example.PopcornCinema.service.ComboService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Controller
@CrossOrigin(origins = "*")
public class ComboController {

    private final ComboService comboService;

    public ComboController(ComboService comboService) {
        this.comboService = comboService;
    }

    @GetMapping("/api/combos")
    @ResponseBody
    public ResponseEntity<List<ComboDto>> getActiveCombos() {
        return ResponseEntity.ok(comboService.getActiveCombos());
    }

    @GetMapping("/admin/combo")
    public String list(Model model) {
        model.addAttribute("combos", comboService.getAll());
        model.addAttribute("activePage", "combo");
        return "admin/combo/list";
    }

    @GetMapping("/admin/combo/add")
    public String addForm(@RequestParam(required = false) Long id, Model model) {
        model.addAttribute("combo", id != null ? comboService.getById(id) : new Combo());
        model.addAttribute("activePage", "combo");
        return "admin/combo/add";
    }

    @PostMapping("/admin/combo/save")
    public String save(@ModelAttribute Combo combo,
                       @RequestParam("imageFile") MultipartFile file) {
        try {
            if (file != null && !file.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path uploadPath = Paths.get("src/main/resources/static/uploads");
                Files.createDirectories(uploadPath);
                Files.copy(file.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                combo.setImage("/uploads/" + fileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (combo.getName() == null || combo.getName().isBlank()) {
            combo.setName("Combo mặc định");
        }
        if (combo.getPrice() == null) {
            combo.setPrice(BigDecimal.ZERO);
        }
        if (combo.getStatus() == null || combo.getStatus().isBlank()) {
            combo.setStatus("ACTIVE");
        }

        comboService.save(combo);
        return "redirect:/admin/combo";
    }

    @GetMapping("/admin/combo/delete/{id}")
    public String delete(@PathVariable Long id) {
        comboService.delete(id);
        return "redirect:/admin/combo";
    }
}
