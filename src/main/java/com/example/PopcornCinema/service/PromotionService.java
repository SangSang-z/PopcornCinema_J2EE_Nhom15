package com.example.PopcornCinema.service;

import com.example.PopcornCinema.dto.PromotionDto;
import java.util.List;

public interface PromotionService {
    List<PromotionDto> getActivePromotions();
}