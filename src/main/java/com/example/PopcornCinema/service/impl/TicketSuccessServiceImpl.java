package com.example.PopcornCinema.service.impl;

import com.example.PopcornCinema.dto.TicketSuccessResponse;
import com.example.PopcornCinema.entity.Booking;
import com.example.PopcornCinema.entity.BookingCombo;
import com.example.PopcornCinema.entity.Combo;
import com.example.PopcornCinema.entity.Payment;
import com.example.PopcornCinema.entity.PaymentTransaction;
import com.example.PopcornCinema.repository.BookingComboRepository;
import com.example.PopcornCinema.repository.BookingRepository;
import com.example.PopcornCinema.repository.ComboRepository;
import com.example.PopcornCinema.repository.PaymentRepository;
import com.example.PopcornCinema.repository.PaymentTransactionRepository;
import com.example.PopcornCinema.repository.SeatHoldRepository;
import com.example.PopcornCinema.repository.ShowtimeRepository;
import com.example.PopcornCinema.repository.projection.CheckoutShowtimeProjection;
import com.example.PopcornCinema.repository.projection.HeldSeatSummaryProjection;
import com.example.PopcornCinema.service.TicketSuccessService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TicketSuccessServiceImpl implements TicketSuccessService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final ShowtimeRepository showtimeRepository;
    private final SeatHoldRepository seatHoldRepository;
    private final BookingComboRepository bookingComboRepository;
    private final ComboRepository comboRepository;
    private final JdbcTemplate jdbcTemplate;

    public TicketSuccessServiceImpl(BookingRepository bookingRepository,
                                    PaymentRepository paymentRepository,
                                    PaymentTransactionRepository paymentTransactionRepository,
                                    ShowtimeRepository showtimeRepository,
                                    SeatHoldRepository seatHoldRepository,
                                    BookingComboRepository bookingComboRepository,
                                    ComboRepository comboRepository,
                                    JdbcTemplate jdbcTemplate) {
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.showtimeRepository = showtimeRepository;
        this.seatHoldRepository = seatHoldRepository;
        this.bookingComboRepository = bookingComboRepository;
        this.comboRepository = comboRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public TicketSuccessResponse getSuccessInfo(String orderCode) {
        Booking booking = bookingRepository.findByBookingCode(orderCode).orElse(null);
        PaymentTransaction tx = null;

        if (booking == null) {
            tx = paymentTransactionRepository.findByOrderCode(orderCode).orElse(null);
            if (tx != null && tx.getBookingId() != null) {
                booking = bookingRepository.findById(tx.getBookingId()).orElse(null);
            }
        }

        if (booking != null) {
            return buildSuccessFromBooking(booking);
        }

        if (tx == null) {
            throw new RuntimeException("Khong tim thay booking");
        }

        return buildSuccessFromPendingTransaction(tx);
    }

    private TicketSuccessResponse buildSuccessFromBooking(Booking booking) {
        CheckoutShowtimeProjection showtimeInfo =
                showtimeRepository.findCheckoutInfoByShowtimeId(booking.getShowtimeId());

        if (showtimeInfo == null) {
            throw new RuntimeException("Khong tim thay thong tin suat chieu");
        }

        Payment payment = paymentRepository.findByBookingId(booking.getId()).orElse(null);

        List<String> seatLabels = jdbcTemplate.query("""
            SELECT s.seat_row, s.seat_number
            FROM tickets t
            JOIN seats s ON t.seat_id = s.id
            WHERE t.booking_id = ?
            ORDER BY s.seat_row, s.seat_number
        """, (rs, rowNum) -> rs.getString("seat_row") + rs.getInt("seat_number"), booking.getId());

        String seatsText = (seatLabels == null || seatLabels.isEmpty())
                ? "Chua co ghe"
                : String.join(", ", seatLabels);

        BigDecimal seatTotal = jdbcTemplate.queryForObject("""
            SELECT COALESCE(SUM(unit_price), 0)
            FROM tickets
            WHERE booking_id = ?
        """, BigDecimal.class, booking.getId());

        seatTotal = nvl(seatTotal);

        List<BookingCombo> combos =
            bookingComboRepository.findByBookingIdOrderByIdAsc(booking.getId());
        Map<Long, Combo> comboMap;
        if (combos == null || combos.isEmpty()) {
            comboMap = Collections.emptyMap();
        } else {
            comboMap = comboRepository.findAllById(
                    combos.stream().map(BookingCombo::getComboId).toList()
            ).stream().collect(Collectors.toMap(Combo::getId, c -> c));
        }

        String comboText = (combos == null || combos.isEmpty())
                ? "Khong co"
                : combos.stream()
                        .filter(c -> c.getQuantity() != null && c.getQuantity() > 0)
                        .map(c -> {
                            Combo combo = comboMap.get(c.getComboId());
                            String name = combo != null ? combo.getName() : "Combo";
                            return name + " x" + c.getQuantity();
                        })
                        .collect(Collectors.joining(", "));

        if (comboText == null || comboText.isBlank()) {
            comboText = "Khong co";
        }

        BigDecimal comboTotal = (combos == null ? Collections.<BookingCombo>emptyList() : combos).stream()
                .filter(c -> c.getQuantity() != null && c.getQuantity() > 0)
                .map(c -> {
                    Combo combo = comboMap.get(c.getComboId());
                    BigDecimal price = combo != null ? nvl(combo.getPrice()) : BigDecimal.ZERO;
                    return price.multiply(BigDecimal.valueOf(c.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

        TicketSuccessResponse response = new TicketSuccessResponse();
        response.setBookingCode(booking.getBookingCode());
        response.setMovieTitle(showtimeInfo.getMovieTitle());
        response.setPosterUrl(showtimeInfo.getPosterUrl());
        response.setAgeRating(showtimeInfo.getAgeRating());
        response.setCinemaName(showtimeInfo.getCinemaName());
        response.setAuditoriumName(showtimeInfo.getAuditoriumName());
        response.setShowtimeText(showtimeInfo.getStartTime().format(formatter));
        response.setSeatsText(seatsText);
        response.setComboText(comboText);
        response.setSeatTotal(seatTotal);
        response.setComboTotal(comboTotal);
        response.setDiscountAmount(nvl(booking.getDiscountAmount()));
        response.setTotalAmount(
                booking.getTotalAmount() != null
                        ? booking.getTotalAmount()
                        : seatTotal.add(comboTotal).subtract(nvl(booking.getDiscountAmount()))
        );
        response.setPaymentStatus(payment != null ? payment.getStatus() : "PAID");

        return response;
    }

    private TicketSuccessResponse buildSuccessFromPendingTransaction(PaymentTransaction tx) {
        CheckoutShowtimeProjection showtimeInfo =
                showtimeRepository.findCheckoutInfoByShowtimeId(tx.getShowtimeId());

        if (showtimeInfo == null) {
            throw new RuntimeException("Khong tim thay thong tin suat chieu");
        }

        List<HeldSeatSummaryProjection> heldSeats =
                seatHoldRepository.findHeldSeatSummaryByShowtimeIdAndUserId(tx.getShowtimeId(), tx.getUserId());

        List<String> seatLabels = heldSeats == null
                ? Collections.emptyList()
                : heldSeats.stream()
                    .map(seat -> seat.getSeatRow() + seat.getSeatNumber())
                    .toList();

        String seatsText = seatLabels.isEmpty()
                ? "Chua co ghe"
                : String.join(", ", seatLabels);

        BigDecimal basePrice = nvl(showtimeInfo.getBasePrice());
        BigDecimal seatTotal = heldSeats == null
                ? BigDecimal.ZERO
                : heldSeats.stream()
                    .map(seat -> basePrice.add(nvl(seat.getExtraPrice())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<BookingCombo> combos =
                bookingComboRepository.findByShowtimeIdAndUserIdAndBookingIdIsNullOrderByIdAsc(
                        tx.getShowtimeId(), tx.getUserId()
                );

        Map<Long, Combo> comboMap;
        if (combos == null || combos.isEmpty()) {
            comboMap = Collections.emptyMap();
        } else {
            comboMap = comboRepository.findAllById(
                    combos.stream().map(BookingCombo::getComboId).toList()
            ).stream().collect(Collectors.toMap(Combo::getId, c -> c));
        }

        String comboText = (combos == null || combos.isEmpty())
                ? "Khong co"
                : combos.stream()
                        .filter(c -> c.getQuantity() != null && c.getQuantity() > 0)
                        .map(c -> {
                            Combo combo = comboMap.get(c.getComboId());
                            String name = combo != null ? combo.getName() : "Combo";
                            return name + " x" + c.getQuantity();
                        })
                        .collect(Collectors.joining(", "));

        if (comboText == null || comboText.isBlank()) {
            comboText = "Khong co";
        }

        BigDecimal comboTotal = (combos == null ? Collections.<BookingCombo>emptyList() : combos).stream()
                .filter(c -> c.getQuantity() != null && c.getQuantity() > 0)
                .map(c -> {
                    Combo combo = comboMap.get(c.getComboId());
                    BigDecimal price = combo != null ? nvl(combo.getPrice()) : BigDecimal.ZERO;
                    return price.multiply(BigDecimal.valueOf(c.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal paidAmount = nvl(tx.getAmount());
        BigDecimal discountAmount = seatTotal.add(comboTotal).subtract(paidAmount);
        if (discountAmount.compareTo(BigDecimal.ZERO) < 0) {
            discountAmount = BigDecimal.ZERO;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

        TicketSuccessResponse response = new TicketSuccessResponse();
        response.setBookingCode(tx.getOrderCode());
        response.setMovieTitle(showtimeInfo.getMovieTitle());
        response.setPosterUrl(showtimeInfo.getPosterUrl());
        response.setAgeRating(showtimeInfo.getAgeRating());
        response.setCinemaName(showtimeInfo.getCinemaName());
        response.setAuditoriumName(showtimeInfo.getAuditoriumName());
        response.setShowtimeText(
                showtimeInfo.getStartTime() != null ? showtimeInfo.getStartTime().format(formatter) : ""
        );
        response.setSeatsText(seatsText);
        response.setComboText(comboText);
        response.setSeatTotal(seatTotal);
        response.setComboTotal(comboTotal);
        response.setDiscountAmount(discountAmount);
        response.setTotalAmount(paidAmount);
        String normalizedStatus = tx.getStatus() == null ? "" : tx.getStatus().toUpperCase();
        String paymentStatus = "PAID";
        if ("CANCELLED".equals(normalizedStatus)
                || "EXPIRED".equals(normalizedStatus)
                || "REJECTED".equals(normalizedStatus)
                || "FAILED".equals(normalizedStatus)) {
            paymentStatus = normalizedStatus;
        }
        response.setPaymentStatus(paymentStatus);

        return response;
    }

    private BigDecimal nvl(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
