package com.example.PopcornCinema.controller;

import com.example.PopcornCinema.dto.ComboDto;
import com.example.PopcornCinema.service.ComboService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/combos")
@CrossOrigin(origins = "*")
public class ComboController {

    private final ComboService comboService;

    public ComboController(ComboService comboService) {
        this.comboService = comboService;
    }

    @GetMapping
    public ResponseEntity<List<ComboDto>> getActiveCombos() {
        return ResponseEntity.ok(comboService.getActiveCombos());
    }
}