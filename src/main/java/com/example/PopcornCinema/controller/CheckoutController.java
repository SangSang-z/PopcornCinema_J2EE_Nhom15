package com.example.PopcornCinema.controller;

import com.example.PopcornCinema.dto.CheckoutSummaryResponse;
import com.example.PopcornCinema.service.CheckoutService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            @RequestParam(value = "promotionId", required = false) Long promotionId
    ) {
        return ResponseEntity.ok(checkoutService.getSummary(showtimeId, userId, promotionId));
    }
}