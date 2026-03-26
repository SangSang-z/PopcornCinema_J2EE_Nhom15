package com.example.PopcornCinema.service;

import com.example.PopcornCinema.dto.ComboDto;
import java.util.List;

public interface ComboService {
    List<ComboDto> getActiveCombos();
}