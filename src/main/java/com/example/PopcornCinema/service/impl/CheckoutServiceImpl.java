package com.example.PopcornCinema.service.impl;

import com.example.PopcornCinema.dto.CheckoutSummaryResponse;
import com.example.PopcornCinema.entity.Promotion;
import com.example.PopcornCinema.respository.BookingComboRepository;
import com.example.PopcornCinema.respository.PromotionRepository;
import com.example.PopcornCinema.respository.SeatHoldRepository;
import com.example.PopcornCinema.respository.ShowtimeRepository;
import com.example.PopcornCinema.respository.projection.CheckoutShowtimeProjection;
import com.example.PopcornCinema.respository.projection.HeldSeatSummaryProjection;
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

    public CheckoutServiceImpl(ShowtimeRepository showtimeRepository,
                               SeatHoldRepository seatHoldRepository,
                               BookingComboRepository bookingComboRepository,
                               PromotionRepository promotionRepository) {
        this.showtimeRepository = showtimeRepository;
        this.seatHoldRepository = seatHoldRepository;
        this.bookingComboRepository = bookingComboRepository;
        this.promotionRepository = promotionRepository;
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

        BigDecimal discountAmount = calculateDiscount(promotionId, seatTotal.add(comboTotal));
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

    private BigDecimal calculateDiscount(Long promotionId, BigDecimal subtotal) {
        if (promotionId == null) {
            return BigDecimal.ZERO;
        }

        Promotion promotion = promotionRepository.findById(promotionId).orElse(null);
        if (promotion == null) {
            return BigDecimal.ZERO;
        }

        if (!"ACTIVE".equalsIgnoreCase(promotion.getStatus())) {
            return BigDecimal.ZERO;
        }

        BigDecimal discount = BigDecimal.ZERO;

        if (promotion.getDiscountAmount() != null) {
            discount = promotion.getDiscountAmount();
        } else if (promotion.getDiscountPercent() != null) {
            discount = subtotal.multiply(promotion.getDiscountPercent())
                    .divide(BigDecimal.valueOf(100));
        }

        if (discount.compareTo(subtotal) > 0) {
            discount = subtotal;
        }

        return discount;
    }
}