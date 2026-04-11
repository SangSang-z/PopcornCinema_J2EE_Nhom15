package com.example.PopcornCinema.controller;

import com.example.PopcornCinema.dto.PromotionDto;
import com.example.PopcornCinema.entity.User;
import com.example.PopcornCinema.repository.BookingRepository;
import com.example.PopcornCinema.service.PromotionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/promotions")
@CrossOrigin(origins = "*")
public class PromotionController {

    private final PromotionService promotionService;
    private final BookingRepository bookingRepository;

    public PromotionController(PromotionService promotionService,
                               BookingRepository bookingRepository) {
        this.promotionService = promotionService;
        this.bookingRepository = bookingRepository;
    }

    @GetMapping("/active")
    public ResponseEntity<List<PromotionDto>> getActivePromotions(HttpSession session) {
        Set<Long> savedPromotions = getSavedPromotions(session);
        if (savedPromotions.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        List<PromotionDto> promotions = promotionService.getActivePromotions();
        Long userId = getCurrentUserId(session);
        Set<Long> usedPromotions = getUsedPromotionIds(userId, savedPromotions);

        List<PromotionDto> filtered = promotions.stream()
                .filter(p -> savedPromotions.contains(p.getId()))
                .filter(p -> !usedPromotions.contains(p.getId()))
                .toList();

        return ResponseEntity.ok(filtered);
    }

    private Long getCurrentUserId(HttpSession session) {
        Object stored = session.getAttribute("user");
        if (stored instanceof User user) {
            return user.getId();
        }
        return null;
    }

    private Set<Long> getUsedPromotionIds(Long userId, Set<Long> savedPromotions) {
        if (userId == null || savedPromotions.isEmpty()) {
            return Set.of();
        }
        List<Long> ids = bookingRepository.findUsedPromotionIds(userId, new ArrayList<>(savedPromotions), "CONFIRMED");
        return new HashSet<>(ids);
    }

    @SuppressWarnings("unchecked")
    private Set<Long> getSavedPromotions(HttpSession session) {
        Object stored = session.getAttribute("savedPromotions");
        if (stored instanceof Set) {
            return (Set<Long>) stored;
        }
        Set<Long> fresh = new HashSet<>();
        session.setAttribute("savedPromotions", fresh);
        return fresh;
    }

}
