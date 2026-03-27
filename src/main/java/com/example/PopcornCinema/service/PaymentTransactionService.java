package com.example.PopcornCinema.service;

import com.example.PopcornCinema.dto.CreatePaymentTransactionRequest;
import com.example.PopcornCinema.dto.PaymentTransactionResponse;
import com.example.PopcornCinema.dto.PaymentTransactionStatusResponse;

public interface PaymentTransactionService {
    PaymentTransactionResponse createTransaction(Long showtimeId, CreatePaymentTransactionRequest request);
    PaymentTransactionStatusResponse getStatus(String orderCode);
    void cancel(String orderCode);

    // user bấm "Tôi đã thanh toán"
    void markSubmitted(String orderCode);

    // admin xác nhận / từ chối
    void confirmByAdmin(String orderCode);
    void rejectByAdmin(String orderCode);
}