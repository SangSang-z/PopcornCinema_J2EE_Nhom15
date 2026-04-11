package com.example.PopcornCinema.service.impl;

import com.example.PopcornCinema.dto.CheckoutSummaryResponse;
import com.example.PopcornCinema.entity.Promotion;
import com.example.PopcornCinema.repository.BookingRepository;
import com.example.PopcornCinema.repository.BookingComboRepository;
import com.example.PopcornCinema.repository.PromotionRepository;
import com.example.PopcornCinema.repository.SeatHoldRepository;
import com.example.PopcornCinema.repository.ShowtimeRepository;
import com.example.PopcornCinema.repository.projection.CheckoutShowtimeProjection;
import com.example.PopcornCinema.repository.projection.HeldSeatSummaryProjection;
import com.example.PopcornCinema.service.CheckoutService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CheckoutServiceImpl implements CheckoutService {

    private final ShowtimeRepository showtimeRepository;
    private final SeatHoldRepository seatHoldRepository;
    private final BookingComboRepository bookingComboRepository;
    private final PromotionRepository promotionRepository;
    private final BookingRepository bookingRepository;

    public CheckoutServiceImpl(ShowtimeRepository showtimeRepository,
                               SeatHoldRepository seatHoldRepository,
                               BookingComboRepository bookingComboRepository,
                               PromotionRepository promotionRepository,
                               BookingRepository bookingRepository) {
        this.showtimeRepository = showtimeRepository;
        this.seatHoldRepository = seatHoldRepository;
        this.bookingComboRepository = bookingComboRepository;
        this.promotionRepository = promotionRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public CheckoutSummaryResponse getSummary(Long showtimeId, Long userId, Long promotionId) {
        CheckoutShowtimeProjection info = showtimeRepository.findCheckoutInfoByShowtimeId(showtimeId);
        if (info == null) {
            throw new RuntimeException("Không tìm thấy suất chiếu");
        }

        List<HeldSeatSummaryProjection> heldSeats =
                seatHoldRepository.findHeldSeatSummaryByShowtimeIdAndUserId(showtimeId, userId);

        BigDecimal seatTotal = heldSeats.stream()
                .map(seat -> info.getBasePrice().add(
                        seat.getExtraPrice() == null ? BigDecimal.ZERO : seat.getExtraPrice()
                ))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal comboTotal = bookingComboRepository.calculateTempComboTotal(showtimeId, userId);
        if (comboTotal == null) {
            comboTotal = BigDecimal.ZERO;
        }

        BigDecimal discountAmount = calculateDiscount(userId, promotionId, seatTotal.add(comboTotal));
        BigDecimal totalAmount = seatTotal.add(comboTotal).subtract(discountAmount);
        if (totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            totalAmount = BigDecimal.ZERO;
        }

        String seatsText = heldSeats.stream()
                .map(seat -> seat.getSeatRow() + seat.getSeatNumber())
                .collect(Collectors.joining(", "));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

        CheckoutSummaryResponse response = new CheckoutSummaryResponse();
        response.setMovieTitle(info.getMovieTitle());
        response.setPosterUrl(info.getPosterUrl());
        response.setAgeRating(info.getAgeRating());
        response.setCinemaName(info.getCinemaName());
        response.setAuditoriumName(info.getAuditoriumName());
        response.setStartTimeText(info.getStartTime().format(formatter));
        response.setSeatsText(seatsText.isBlank() ? "Chưa chọn" : seatsText);
        response.setSeatTotal(seatTotal);
        response.setComboTotal(comboTotal);
        response.setDiscountAmount(discountAmount);
        response.setTotalAmount(totalAmount);

        return response;
    }

    private BigDecimal calculateDiscount(Long userId, Long promotionId, BigDecimal subtotal) {
        if (promotionId == null) {
            return BigDecimal.ZERO;
        }

        if (userId != null
                && bookingRepository.existsByUserIdAndPromotionIdAndStatusIgnoreCase(userId, promotionId, "CONFIRMED")) {
            return BigDecimal.ZERO;
        }

        Promotion promotion = promotionRepository.findById(promotionId).orElse(null);
        if (promotion == null) {
            return BigDecimal.ZERO;
        }

        if (!"ACTIVE".equalsIgnoreCase(promotion.getStatus())) {
            return BigDecimal.ZERO;
        }

        java.time.LocalDateTime now = java.time.LocalDateTime.now();

        if (promotion.getStartDate() != null && now.isBefore(promotion.getStartDate())) {
            return BigDecimal.ZERO;
        }

        if (promotion.getEndDate() != null && now.isAfter(promotion.getEndDate())) {
            return BigDecimal.ZERO;
        }

        if (promotion.getMinOrderValue() != null) {
            BigDecimal minOrderValue = promotion.getMinOrderValue();
            if (subtotal.compareTo(minOrderValue) < 0) {
                throw new RuntimeException("Đơn hàng chưa đạt giá trị tối thiểu " + minOrderValue + " VND");
            }
        }

        BigDecimal discount = BigDecimal.ZERO;

        if (promotion.getDiscountAmount() != null) {
            discount = promotion.getDiscountAmount();
        } else if (promotion.getDiscountPercent() != null) {
            discount = subtotal.multiply(promotion.getDiscountPercent())
                    .divide(BigDecimal.valueOf(100), 0, java.math.RoundingMode.HALF_UP);
        }

        if (discount.compareTo(subtotal) > 0) {
            discount = subtotal;
        }

        return discount;
    }
}
