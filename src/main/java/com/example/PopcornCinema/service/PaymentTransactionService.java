package com.example.PopcornCinema.service;

import com.example.PopcornCinema.dto.CreatePaymentTransactionRequest;
import com.example.PopcornCinema.dto.PaymentTransactionResponse;
import com.example.PopcornCinema.dto.PaymentTransactionStatusResponse;

public interface PaymentTransactionService {
    PaymentTransactionResponse createTransaction(Long showtimeId, CreatePaymentTransactionRequest request);
    PaymentTransactionStatusResponse getStatus(String orderCode);
    void cancel(String orderCode);
    void mockPaid(String orderCode);
}