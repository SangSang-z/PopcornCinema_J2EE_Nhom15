package com.example.PopcornCinema.service.impl;

import com.example.PopcornCinema.dto.ComboDto;
import com.example.PopcornCinema.repository.ComboRepository;
import com.example.PopcornCinema.service.ComboService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComboServiceImpl implements ComboService {

    private final ComboRepository comboRepository;

    public ComboServiceImpl(ComboRepository comboRepository) {
        this.comboRepository = comboRepository;
    }


    @Override
    public List<com.example.PopcornCinema.entity.Combo> getAll() {
        return comboRepository.findAll();
    }

    @Override
    public void save(com.example.PopcornCinema.entity.Combo combo) {
        comboRepository.save(combo);
    }

    @Override
    public void delete(Long id) {
        comboRepository.deleteById(id);
    }

    @Override
    public com.example.PopcornCinema.entity.Combo getById(Long id) {
        return comboRepository.findById(id).orElse(null);
    }

    @Override
    public List<ComboDto> getActiveCombos() {
        return comboRepository.findByStatusOrderByIdAsc("ACTIVE")
                .stream()
                .map(combo -> new ComboDto(
                        combo.getId(),
                        combo.getName(),
                        combo.getDescription(),
                        combo.getPrice(),
                        combo.getImageUrl()
                ))
                .toList();
    }
}