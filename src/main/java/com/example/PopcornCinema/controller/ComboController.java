package com.example.PopcornCinema.controller;

import com.example.PopcornCinema.entity.Combo;
import com.example.PopcornCinema.service.ComboService;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;            
import org.springframework.ui.Model;                         
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;    

@Controller
@RequestMapping("/admin/combo")
public class ComboController {

    @Autowired
    private ComboService comboService;

    // ================= LIST =================
    @GetMapping
    public String list(Model model){
        model.addAttribute("combos", comboService.getAll());
        model.addAttribute("activePage", "combo"); // ✅ FIX sidebar
        return "admin/combo/list";
    }

    // ================= ADD / EDIT =================
    @GetMapping("/add")
    public String addForm(@RequestParam(required = false) Long id, Model model) {

        if(id != null){
            model.addAttribute("combo", comboService.getById(id));
        } else {
            model.addAttribute("combo", new Combo());
        }

        model.addAttribute("activePage", "combo");
        return "admin/combo/add";
    }

    // ================= SAVE =================
    @PostMapping("/save")
    public String save(@ModelAttribute Combo combo,
                    @RequestParam("imageFile") MultipartFile file) {

        try {
            if (!file.isEmpty()) {

                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

                Path uploadPath = Paths.get("src/main/resources/static/uploads");

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                Files.copy(file.getInputStream(), uploadPath.resolve(fileName));

                combo.setImage("/uploads/" + fileName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // chống null
        if(combo.getName() == null || combo.getName().isEmpty()){
            combo.setName("Combo mặc định");
        }

        if(combo.getPrice() == null){
            combo.setPrice(0.0);
        }

        if(combo.getStatus() == null){
            combo.setStatus("ACTIVE");
        }

        comboService.save(combo);
        return "redirect:/admin/combo";
    }

    // ================= DELETE =================
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id){
        comboService.delete(id);
        return "redirect:/admin/combo";
    }
}
