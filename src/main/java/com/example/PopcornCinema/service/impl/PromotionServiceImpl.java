package com.example.PopcornCinema.service.impl;

import com.example.PopcornCinema.dto.PromotionDto;
import com.example.PopcornCinema.repository.PromotionRepository;
import com.example.PopcornCinema.service.PromotionService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;

    public PromotionServiceImpl(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    @Override
    public List<PromotionDto> getActivePromotions() {
        LocalDateTime now = LocalDateTime.now();
        return promotionRepository.findActivePromotions()
                .stream()
                .filter(p -> p.getStartDate() == null || !now.isBefore(p.getStartDate()))
                .filter(p -> p.getEndDate() == null || !now.isAfter(p.getEndDate()))
                .map(p -> new PromotionDto(
                        p.getId(),
                        p.getCode(),
                        p.getTitle(),
                        p.getDescription(),
                        p.getDiscountPercent(),
                        p.getDiscountAmount()
                ))
                .toList();
    }
}
