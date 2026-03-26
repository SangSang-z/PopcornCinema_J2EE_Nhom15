package com.example.PopcornCinema.service.impl;

import com.example.PopcornCinema.dto.CheckoutSummaryResponse;
import com.example.PopcornCinema.dto.CreatePaymentTransactionRequest;
import com.example.PopcornCinema.dto.PaymentTransactionResponse;
import com.example.PopcornCinema.dto.PaymentTransactionStatusResponse;
import com.example.PopcornCinema.entity.PaymentTransaction;
import com.example.PopcornCinema.respository.PaymentTransactionRepository;
import com.example.PopcornCinema.service.BookingFinalizeService;
import com.example.PopcornCinema.service.CheckoutService;
import com.example.PopcornCinema.service.PaymentTransactionService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentTransactionServiceImpl implements PaymentTransactionService {

    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_PAID = "PAID";
    private static final String STATUS_CANCELLED = "CANCELLED";
    private static final String STATUS_EXPIRED = "EXPIRED";

    private static final String BANK_ID = "MB";
    private static final String ACCOUNT_NO = "123456789";
    private static final String ACCOUNT_NAME = "POPCORN CINEMA";
    private static final String QR_TEMPLATE = "compact2";
    private static final long EXPIRE_MINUTES = 5;

    private final PaymentTransactionRepository paymentTransactionRepository;
    private final CheckoutService checkoutService;
    private final BookingFinalizeService bookingFinalizeService;

    public PaymentTransactionServiceImpl(PaymentTransactionRepository paymentTransactionRepository,
                                         CheckoutService checkoutService,
                                         BookingFinalizeService bookingFinalizeService) {
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.checkoutService = checkoutService;
        this.bookingFinalizeService = bookingFinalizeService;
    }

    @Override
    @Transactional
    public PaymentTransactionResponse createTransaction(Long showtimeId, CreatePaymentTransactionRequest request) {
        Long userId = request.getUserId();

        List<PaymentTransaction> pendingTransactions =
                paymentTransactionRepository.findByUserIdAndShowtimeIdAndStatus(userId, showtimeId, STATUS_PENDING);

        for (PaymentTransaction existingTx : pendingTransactions) {
            if (existingTx.getExpiresAt() != null && existingTx.getExpiresAt().isAfter(LocalDateTime.now())) {
                existingTx.setStatus(STATUS_CANCELLED);
                paymentTransactionRepository.save(existingTx);
            }
        }

        CheckoutSummaryResponse summary = checkoutService.getSummary(showtimeId, userId, request.getPromotionId());
        BigDecimal totalAmount = summary.getTotalAmount() == null ? BigDecimal.ZERO : summary.getTotalAmount();

        String orderCode = generateOrderCode();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(EXPIRE_MINUTES);

        PaymentTransaction tx = new PaymentTransaction();
        tx.setOrderCode(orderCode);
        tx.setUserId(userId);
        tx.setShowtimeId(showtimeId);
        tx.setAmount(totalAmount);
        tx.setStatus(STATUS_PENDING);
        tx.setQrContent(buildTransferContent(orderCode, totalAmount));
        tx.setQrImageUrl(buildVietQrImageUrl(orderCode, totalAmount));
        tx.setExpiresAt(expiresAt);

        paymentTransactionRepository.save(tx);
        return toResponse(tx);
    }

    @Override
    @Transactional
    public PaymentTransactionStatusResponse getStatus(String orderCode) {
        PaymentTransaction tx = getTransactionOrThrow(orderCode);
        markExpiredIfNeeded(tx);

        PaymentTransactionStatusResponse response = new PaymentTransactionStatusResponse();
        response.setOrderCode(tx.getOrderCode());
        response.setStatus(tx.getStatus());
        response.setAmount(tx.getAmount());
        response.setExpiresAt(tx.getExpiresAt());
        return response;
    }

    @Override
    @Transactional
    public void cancel(String orderCode) {
        PaymentTransaction tx = getTransactionOrThrow(orderCode);
        markExpiredIfNeeded(tx);

        if (STATUS_PENDING.equals(tx.getStatus())) {
            tx.setStatus(STATUS_CANCELLED);
            paymentTransactionRepository.save(tx);
        }
    }

    @Override
    @Transactional
    public void mockPaid(String orderCode) {
        PaymentTransaction tx = getTransactionOrThrow(orderCode);
        markExpiredIfNeeded(tx);

        if (STATUS_EXPIRED.equals(tx.getStatus()) || STATUS_CANCELLED.equals(tx.getStatus())) {
            throw new RuntimeException("Giao dịch đã hết hạn hoặc đã bị hủy, không thể xác nhận thanh toán");
        }

        if (!STATUS_PAID.equals(tx.getStatus())) {
            tx.setStatus(STATUS_PAID);
            tx.setPaidAt(LocalDateTime.now());
            paymentTransactionRepository.save(tx);
        }

        bookingFinalizeService.finalizeSuccessfulPayment(orderCode);
    }

    private PaymentTransaction getTransactionOrThrow(String orderCode) {
        return paymentTransactionRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch"));
    }

    private void markExpiredIfNeeded(PaymentTransaction tx) {
        if (STATUS_PENDING.equals(tx.getStatus())
                && tx.getExpiresAt() != null
                && tx.getExpiresAt().isBefore(LocalDateTime.now())) {
            tx.setStatus(STATUS_EXPIRED);
            paymentTransactionRepository.save(tx);
        }
    }

    private PaymentTransactionResponse toResponse(PaymentTransaction tx) {
        PaymentTransactionResponse response = new PaymentTransactionResponse();
        response.setOrderCode(tx.getOrderCode());
        response.setAmount(tx.getAmount());
        response.setStatus(tx.getStatus());
        response.setQrContent(tx.getQrContent());
        response.setQrImageUrl(tx.getQrImageUrl());
        response.setExpiresAt(tx.getExpiresAt());
        return response;
    }

    private String generateOrderCode() {
        return "PC" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    private String buildTransferContent(String orderCode, BigDecimal amount) {
        return "BANK=" + BANK_ID
                + ";ACC=" + ACCOUNT_NO
                + ";NAME=" + ACCOUNT_NAME
                + ";AMOUNT=" + normalizeAmount(amount)
                + ";DESC=" + orderCode;
    }

    private String buildVietQrImageUrl(String orderCode, BigDecimal amount) {
        String encodedAccountName = URLEncoder.encode(ACCOUNT_NAME, StandardCharsets.UTF_8);
        String encodedOrderCode = URLEncoder.encode(orderCode, StandardCharsets.UTF_8);

        return "https://img.vietqr.io/image/"
                + BANK_ID + "-" + ACCOUNT_NO + "-" + QR_TEMPLATE + ".png"
                + "?amount=" + normalizeAmount(amount)
                + "&addInfo=" + encodedOrderCode
                + "&accountName=" + encodedAccountName;
    }

    private String normalizeAmount(BigDecimal amount) {
        if (amount == null) {
            return "0";
        }
        return amount.setScale(0, java.math.RoundingMode.HALF_UP).toPlainString();
    }
}
