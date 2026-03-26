package com.example.PopcornCinema.service.impl;

import com.example.PopcornCinema.dto.TicketSuccessResponse;
import com.example.PopcornCinema.entity.Booking;
import com.example.PopcornCinema.entity.BookingCombo;
import com.example.PopcornCinema.entity.Combo;
import com.example.PopcornCinema.entity.Payment;
import com.example.PopcornCinema.respository.BookingComboRepository;
import com.example.PopcornCinema.respository.BookingRepository;
import com.example.PopcornCinema.respository.ComboRepository;
import com.example.PopcornCinema.respository.PaymentRepository;
import com.example.PopcornCinema.respository.ShowtimeRepository;
import com.example.PopcornCinema.respository.projection.CheckoutShowtimeProjection;
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
    private final ShowtimeRepository showtimeRepository;
    private final BookingComboRepository bookingComboRepository;
    private final ComboRepository comboRepository;
    private final JdbcTemplate jdbcTemplate;

    public TicketSuccessServiceImpl(BookingRepository bookingRepository,
                                    PaymentRepository paymentRepository,
                                    ShowtimeRepository showtimeRepository,
                                    BookingComboRepository bookingComboRepository,
                                    ComboRepository comboRepository,
                                    JdbcTemplate jdbcTemplate) {
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
        this.showtimeRepository = showtimeRepository;
        this.bookingComboRepository = bookingComboRepository;
        this.comboRepository = comboRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public TicketSuccessResponse getSuccessInfo(String orderCode) {
        Booking booking = bookingRepository.findByBookingCode(orderCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy booking"));

        CheckoutShowtimeProjection showtimeInfo =
                showtimeRepository.findCheckoutInfoByShowtimeId(booking.getShowtimeId());

        if (showtimeInfo == null) {
            throw new RuntimeException("Không tìm thấy thông tin suất chiếu");
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
                ? "Chưa có ghế"
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
                ? "Không có"
                : combos.stream()
                        .filter(c -> c.getQuantity() != null && c.getQuantity() > 0)
                        .map(c -> {
                            Combo combo = comboMap.get(c.getComboId());
                            String name = combo != null ? combo.getName() : "Combo";
                            return name + " x" + c.getQuantity();
                        })
                        .collect(Collectors.joining(", "));

        if (comboText == null || comboText.isBlank()) {
            comboText = "Không có";
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

    private BigDecimal nvl(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}