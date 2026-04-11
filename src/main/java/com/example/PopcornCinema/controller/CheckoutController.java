package com.example.PopcornCinema.controller;

import com.example.PopcornCinema.dto.CheckoutSummaryResponse;
import com.example.PopcornCinema.service.CheckoutService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api/showtimes")
@CrossOrigin(origins = "*")
public class CheckoutController {

    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @GetMapping("/{showtimeId}/checkout-summary")
    public ResponseEntity<CheckoutSummaryResponse> getCheckoutSummary(
            @PathVariable Long showtimeId,
            @RequestParam("userId") Long userId,
            @RequestParam(value = "promotionId", required = false) Long promotionId,
            HttpSession session
    ) {
        Set<Long> savedPromotions = getSavedPromotions(session);
        if (promotionId != null && !savedPromotions.contains(promotionId)) {
            promotionId = null;
        }
        return ResponseEntity.ok(checkoutService.getSummary(showtimeId, userId, promotionId));
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
