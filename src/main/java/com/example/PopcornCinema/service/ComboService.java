package com.example.PopcornCinema.service;

import com.example.PopcornCinema.dto.ComboDto;
import com.example.PopcornCinema.entity.Combo;

import java.util.List;

public interface ComboService {
    List<ComboDto> getActiveCombos();
    List<Combo> getAll();
    void save(Combo combo);
    void delete(Long id);
    Combo getById(Long id);
}
