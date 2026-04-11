package com.example.PopcornCinema.controller;

import com.example.PopcornCinema.dto.CreatePaymentTransactionRequest;
import com.example.PopcornCinema.dto.PaymentTransactionResponse;
import com.example.PopcornCinema.dto.PaymentTransactionStatusResponse;
import com.example.PopcornCinema.service.PaymentTransactionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class PaymentTransactionController {

    private final PaymentTransactionService paymentTransactionService;

    public PaymentTransactionController(PaymentTransactionService paymentTransactionService) {
        this.paymentTransactionService = paymentTransactionService;
    }

    @PostMapping("/showtimes/{showtimeId}/payment-transactions")
    public ResponseEntity<PaymentTransactionResponse> createTransaction(
            @PathVariable Long showtimeId,
            @RequestBody CreatePaymentTransactionRequest request,
            HttpSession session
    ) {
        Set<Long> savedPromotions = getSavedPromotions(session);
        if (request.getPromotionId() != null && !savedPromotions.contains(request.getPromotionId())) {
            request.setPromotionId(null);
        }
        return ResponseEntity.ok(paymentTransactionService.createTransaction(showtimeId, request));
    }

    @GetMapping("/payment-transactions/{orderCode}/status")
    public ResponseEntity<PaymentTransactionStatusResponse> getStatus(@PathVariable String orderCode) {
        return ResponseEntity.ok(paymentTransactionService.getStatus(orderCode));
    }

    @PostMapping("/payment-transactions/{orderCode}/cancel")
    public ResponseEntity<?> cancel(@PathVariable String orderCode) {
        paymentTransactionService.cancel(orderCode);
        return ResponseEntity.ok(Map.of("message", "ÄÃ£ há»§y giao dá»‹ch"));
    }

    @PostMapping("/payment-transactions/{orderCode}/mark-submitted")
    public ResponseEntity<?> markSubmitted(@PathVariable String orderCode) {
        paymentTransactionService.markSubmitted(orderCode);
        return ResponseEntity.ok(Map.of("message", "ÄÃ£ ghi nháº­n ngÆ°á»i dÃ¹ng bÃ¡o Ä‘Ã£ thanh toÃ¡n"));
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
