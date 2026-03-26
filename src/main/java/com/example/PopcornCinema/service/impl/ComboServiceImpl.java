package com.example.PopcornCinema.service.impl;

import com.example.PopcornCinema.dto.ComboDto;
import com.example.PopcornCinema.respository.ComboRepository;
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