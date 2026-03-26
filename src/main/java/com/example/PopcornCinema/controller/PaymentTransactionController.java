package com.example.PopcornCinema.controller;

import com.example.PopcornCinema.dto.CreatePaymentTransactionRequest;
import com.example.PopcornCinema.dto.PaymentTransactionResponse;
import com.example.PopcornCinema.dto.PaymentTransactionStatusResponse;
import com.example.PopcornCinema.service.PaymentTransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
            @RequestBody CreatePaymentTransactionRequest request
    ) {
        return ResponseEntity.ok(paymentTransactionService.createTransaction(showtimeId, request));
    }

    @GetMapping("/payment-transactions/{orderCode}/status")
    public ResponseEntity<PaymentTransactionStatusResponse> getStatus(@PathVariable String orderCode) {
        return ResponseEntity.ok(paymentTransactionService.getStatus(orderCode));
    }

    @PostMapping("/payment-transactions/{orderCode}/cancel")
    public ResponseEntity<?> cancel(@PathVariable String orderCode) {
        paymentTransactionService.cancel(orderCode);
        return ResponseEntity.ok(Map.of("message", "Đã hủy giao dịch"));
    }

    @PostMapping("/payment-transactions/{orderCode}/mock-paid")
    public ResponseEntity<?> mockPaid(@PathVariable String orderCode) {
        paymentTransactionService.mockPaid(orderCode);
        return ResponseEntity.ok(Map.of("message", "Đã giả lập thanh toán thành công"));
    }
}