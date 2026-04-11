package com.example.PopcornCinema.service.impl;

import com.example.PopcornCinema.dto.CheckoutSummaryResponse;
import com.example.PopcornCinema.dto.CreatePaymentTransactionRequest;
import com.example.PopcornCinema.dto.PaymentTransactionResponse;
import com.example.PopcornCinema.dto.PaymentTransactionStatusResponse;
import com.example.PopcornCinema.entity.PaymentTransaction;
import com.example.PopcornCinema.repository.BookingComboRepository;
import com.example.PopcornCinema.repository.PaymentTransactionRepository;
import com.example.PopcornCinema.repository.SeatHoldRepository;
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

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_PENDING_CONFIRMATION = "PENDING_CONFIRMATION";
    public static final String STATUS_PAID = "PAID";
    public static final String STATUS_CANCELLED = "CANCELLED";
    public static final String STATUS_EXPIRED = "EXPIRED";
    public static final String STATUS_REJECTED = "REJECTED";
    public static final String STATUS_FAILED = "FAILED";

    private static final String BANK_ID = "VCB";
    private static final String ACCOUNT_NO = "1030472376";
    private static final String ACCOUNT_NAME = "POPCORN CINEMA";
    private static final String QR_TEMPLATE = "compact2";
    private static final long EXPIRE_MINUTES = 2;

    private final PaymentTransactionRepository paymentTransactionRepository;
    private final CheckoutService checkoutService;
    private final BookingFinalizeService bookingFinalizeService;
    private final SeatHoldRepository seatHoldRepository;
    private final BookingComboRepository bookingComboRepository;

    public PaymentTransactionServiceImpl(PaymentTransactionRepository paymentTransactionRepository,
                                         CheckoutService checkoutService,
                                         BookingFinalizeService bookingFinalizeService,
                                         SeatHoldRepository seatHoldRepository,
                                         BookingComboRepository bookingComboRepository) {
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.checkoutService = checkoutService;
        this.bookingFinalizeService = bookingFinalizeService;
        this.seatHoldRepository = seatHoldRepository;
        this.bookingComboRepository = bookingComboRepository;
    }

    @Override
    @Transactional
    public PaymentTransactionResponse createTransaction(Long showtimeId, CreatePaymentTransactionRequest request) {
        Long userId = request.getUserId();

        List<PaymentTransaction> activeTransactions =
                paymentTransactionRepository.findByUserIdAndShowtimeIdAndStatuses(
                        userId,
                        showtimeId,
                        List.of(STATUS_PENDING, STATUS_PENDING_CONFIRMATION)
                );

        for (PaymentTransaction existingTx : activeTransactions) {
            if (existingTx.getExpiresAt() != null && existingTx.getExpiresAt().isAfter(LocalDateTime.now())) {
                existingTx.setStatus(STATUS_CANCELLED);
                paymentTransactionRepository.save(existingTx);
            }
        }

        CheckoutSummaryResponse summary = checkoutService.getSummary(showtimeId, userId, request.getPromotionId());
        BigDecimal totalAmount = summary.getTotalAmount() == null ? BigDecimal.ZERO : summary.getTotalAmount();
        BigDecimal discountAmount = summary.getDiscountAmount() == null ? BigDecimal.ZERO : summary.getDiscountAmount();

        if (request.getPromotionId() != null && discountAmount.compareTo(BigDecimal.ZERO) <= 0) {
            request.setPromotionId(null);
        }

        String orderCode = generateOrderCode();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(EXPIRE_MINUTES);

        PaymentTransaction tx = new PaymentTransaction();
        tx.setOrderCode(orderCode);
        tx.setUserId(userId);
        tx.setShowtimeId(showtimeId);
        tx.setPromotionId(request.getPromotionId());
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

        if (STATUS_PENDING.equals(tx.getStatus()) || STATUS_PENDING_CONFIRMATION.equals(tx.getStatus())) {
            tx.setStatus(STATUS_CANCELLED);
            paymentTransactionRepository.save(tx);

            cleanupTempData(tx);
        }
    }

    @Override
    @Transactional
    public void markSubmitted(String orderCode) {
        PaymentTransaction tx = getTransactionOrThrow(orderCode);
        markExpiredIfNeeded(tx);

        if (STATUS_EXPIRED.equals(tx.getStatus()) || STATUS_CANCELLED.equals(tx.getStatus())) {
            throw new RuntimeException("Giao dịch đã hết hạn hoặc đã bị hủy");
        }

        if (STATUS_PAID.equals(tx.getStatus())) {
            return;
        }

        if (!STATUS_PENDING.equals(tx.getStatus())) {
            throw new RuntimeException("Chỉ giao dịch đang chờ mới được báo đã thanh toán");
        }

        tx.setStatus(STATUS_PENDING_CONFIRMATION);
        paymentTransactionRepository.save(tx);
    }

    @Override
    @Transactional
    public void confirmByAdmin(String orderCode) {
        PaymentTransaction tx = getTransactionOrThrow(orderCode);
        markExpiredIfNeeded(tx);

        if (STATUS_EXPIRED.equals(tx.getStatus()) || STATUS_CANCELLED.equals(tx.getStatus())) {
            throw new RuntimeException("Giao dịch đã hết hạn hoặc đã bị hủy");
        }

        if (!STATUS_PENDING_CONFIRMATION.equals(tx.getStatus())) {
            throw new RuntimeException("Chỉ xác nhận được giao dịch đang chờ admin duyệt");
        }

        tx.setStatus(STATUS_PAID);
        tx.setPaidAt(LocalDateTime.now());
        paymentTransactionRepository.save(tx);

        try {
            bookingFinalizeService.finalizeSuccessfulPayment(orderCode);
        } catch (Exception ex) {
            tx.setStatus(STATUS_FAILED);
            paymentTransactionRepository.save(tx);
            throw new RuntimeException("Xác nhận được thanh toán nhưng tạo booking thất bại: " + ex.getMessage());
        }
    }

    @Override
    @Transactional
    public void rejectByAdmin(String orderCode) {
        PaymentTransaction tx = getTransactionOrThrow(orderCode);
        markExpiredIfNeeded(tx);

        if (!STATUS_PENDING_CONFIRMATION.equals(tx.getStatus())) {
            throw new RuntimeException("Chỉ từ chối được giao dịch đang chờ admin duyệt");
        }

        tx.setStatus(STATUS_REJECTED);
        paymentTransactionRepository.save(tx);

        cleanupTempData(tx);
    }

    private void cleanupTempData(PaymentTransaction tx) {
        seatHoldRepository.deleteByShowtimeIdAndUserId(tx.getShowtimeId(), tx.getUserId());
        bookingComboRepository.deleteByShowtimeIdAndUserIdAndBookingIdIsNull(tx.getShowtimeId(), tx.getUserId());
    }

    private PaymentTransaction getTransactionOrThrow(String orderCode) {
        return paymentTransactionRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch"));
    }

    private void markExpiredIfNeeded(PaymentTransaction tx) {
        if ((STATUS_PENDING.equals(tx.getStatus()) || STATUS_PENDING_CONFIRMATION.equals(tx.getStatus()))
                && tx.getExpiresAt() != null
                && tx.getExpiresAt().isBefore(LocalDateTime.now())) {
            tx.setStatus(STATUS_EXPIRED);
            paymentTransactionRepository.save(tx);
            cleanupTempData(tx);
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
        response.setShowtimeId(tx.getShowtimeId());
        return response;
    }

    private String generateOrderCode() {
        return "ORD" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
    }

    private String buildTransferContent(String orderCode, BigDecimal amount) {
        return orderCode;
    }

    private String buildVietQrImageUrl(String orderCode, BigDecimal amount) {
        String encodedOrderCode = URLEncoder.encode(orderCode, StandardCharsets.UTF_8);
        String encodedAccountName = URLEncoder.encode(ACCOUNT_NAME, StandardCharsets.UTF_8);

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

    @Override
    @Transactional
    public boolean confirmBySepay(String orderCode, BigDecimal amount) {
        PaymentTransaction tx = getTransactionOrThrow(orderCode);
        markExpiredIfNeeded(tx);

        if (STATUS_PAID.equals(tx.getStatus())) {
            return true;
        }

        if (STATUS_EXPIRED.equals(tx.getStatus())
                || STATUS_CANCELLED.equals(tx.getStatus())
                || STATUS_REJECTED.equals(tx.getStatus())
                || STATUS_FAILED.equals(tx.getStatus())) {
            return false;
        }

        if (!isAmountMatched(tx.getAmount(), amount)) {
            return false;
        }

        tx.setStatus(STATUS_PAID);
        tx.setPaidAt(LocalDateTime.now());
        paymentTransactionRepository.save(tx);

        try {
            bookingFinalizeService.finalizeSuccessfulPayment(orderCode);
            return true;
        } catch (Exception ex) {
            tx.setStatus(STATUS_FAILED);
            paymentTransactionRepository.save(tx);
            return false;
        }
    }

    private boolean isAmountMatched(BigDecimal expected, BigDecimal actual) {
        if (expected == null || actual == null) {
            return false;
        }
        BigDecimal expectedRounded = expected.setScale(0, java.math.RoundingMode.HALF_UP);
        BigDecimal actualRounded = actual.setScale(0, java.math.RoundingMode.HALF_UP);
        return actualRounded.compareTo(expectedRounded) >= 0;
    }
}
