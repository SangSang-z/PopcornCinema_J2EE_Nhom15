package com.example.PopcornCinema.service.impl;

import com.example.PopcornCinema.entity.Booking;
import com.example.PopcornCinema.entity.Payment;
import com.example.PopcornCinema.entity.PaymentTransaction;
import com.example.PopcornCinema.repository.BookingComboRepository;
import com.example.PopcornCinema.repository.BookingRepository;
import com.example.PopcornCinema.repository.PaymentRepository;
import com.example.PopcornCinema.repository.PaymentTransactionRepository;
import com.example.PopcornCinema.repository.SeatHoldRepository;
import com.example.PopcornCinema.repository.ShowtimeRepository;
import com.example.PopcornCinema.repository.projection.CheckoutShowtimeProjection;
import com.example.PopcornCinema.repository.projection.HeldSeatSummaryProjection;
import com.example.PopcornCinema.service.BookingFinalizeService;
import jakarta.transaction.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import com.example.PopcornCinema.service.BookingComboService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingFinalizeServiceImpl implements BookingFinalizeService {

    private final PaymentTransactionRepository paymentTransactionRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final SeatHoldRepository seatHoldRepository;
    private final BookingComboRepository bookingComboRepository;
    private final ShowtimeRepository showtimeRepository;
    private final JdbcTemplate jdbcTemplate;

        public BookingFinalizeServiceImpl(PaymentTransactionRepository paymentTransactionRepository,
                                    BookingRepository bookingRepository,
                                    PaymentRepository paymentRepository,
                                    SeatHoldRepository seatHoldRepository,
                                    BookingComboRepository bookingComboRepository,
                                    ShowtimeRepository showtimeRepository,
                                    JdbcTemplate jdbcTemplate,
                                    BookingComboService bookingComboService) {
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
        this.seatHoldRepository = seatHoldRepository;
        this.bookingComboRepository = bookingComboRepository;
        this.showtimeRepository = showtimeRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.bookingComboService = bookingComboService;
    }

    @Override
    @Transactional
    public void finalizeSuccessfulPayment(String orderCode) {
        PaymentTransaction tx = paymentTransactionRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch"));

        if (!"PAID".equalsIgnoreCase(tx.getStatus())) {
            throw new RuntimeException("Giao dịch chưa thanh toán thành công");
        }

        // Đã có booking theo orderCode rồi thì coi như finalize xong
        Booking existingByCode = bookingRepository.findByBookingCode(orderCode).orElse(null);
        if (existingByCode != null) {
            if (tx.getBookingId() == null || !existingByCode.getId().equals(tx.getBookingId())) {
                tx.setBookingId(existingByCode.getId());
                paymentTransactionRepository.save(tx);
            }
            cleanupSeatHolds(tx);
            return;
        }

        // Nếu transaction đã gắn bookingId và booking đó tồn tại thì cũng return để tránh tạo trùng
        if (tx.getBookingId() != null && bookingRepository.findById(tx.getBookingId()).isPresent()) {
            cleanupSeatHolds(tx);
            return;
        }

        CheckoutShowtimeProjection showtimeInfo =
                showtimeRepository.findCheckoutInfoByShowtimeId(tx.getShowtimeId());

        if (showtimeInfo == null) {
            throw new RuntimeException("Không tìm thấy thông tin suất chiếu");
        }

        List<HeldSeatSummaryProjection> heldSeats =
                seatHoldRepository.findHeldSeatSummaryByShowtimeIdAndUserId(tx.getShowtimeId(), tx.getUserId());

        if (heldSeats == null || heldSeats.isEmpty()) {
            throw new RuntimeException("Không tìm thấy ghế đang giữ để tạo vé");
        }

        BigDecimal seatTotal = heldSeats.stream()
                .map(seat -> nvl(showtimeInfo.getBasePrice()).add(nvl(seat.getExtraPrice())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal comboTotal = bookingComboRepository.calculateTempComboTotal(tx.getShowtimeId(), tx.getUserId());
        comboTotal = nvl(comboTotal);

        BigDecimal subtotal = seatTotal.add(comboTotal);
        BigDecimal paidAmount = nvl(tx.getAmount());

        BigDecimal discountAmount = subtotal.subtract(paidAmount);
        if (discountAmount.compareTo(BigDecimal.ZERO) < 0) {
            discountAmount = BigDecimal.ZERO;
        }

        Booking booking = new Booking();
        booking.setBookingCode(tx.getOrderCode());
        booking.setUserId(tx.getUserId());
        booking.setShowtimeId(tx.getShowtimeId());
        booking.setTotalAmount(paidAmount);
        booking.setStatus("CONFIRMED");
        booking.setPromotionId(tx.getPromotionId());
        booking.setDiscountAmount(discountAmount);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());

        bookingRepository.save(booking);
        bookingComboService.moveTempCombosToBooking(tx.getShowtimeId(), tx.getUserId(), booking.getId());

        tx.setBookingId(booking.getId());
        paymentTransactionRepository.save(tx);

        // Insert ticket bằng JDBC để khớp đúng cột DB: unit_price
        for (HeldSeatSummaryProjection seat : heldSeats) {
            BigDecimal unitPrice = nvl(showtimeInfo.getBasePrice()).add(nvl(seat.getExtraPrice()));

            jdbcTemplate.update("""
                INSERT INTO tickets (booking_id, showtime_id, seat_id, unit_price, status)
                VALUES (?, ?, ?, ?, ?)
            """,
                    booking.getId(),
                    tx.getShowtimeId(),
                    seat.getSeatId(),
                    unitPrice,
                    "BOOKED"
            );
        }

        if (paymentRepository.findByBookingId(booking.getId()).isEmpty()) {
            Payment payment = new Payment();
            payment.setBookingId(booking.getId());
            payment.setPaymentMethod("QR");
            payment.setAmount(paidAmount);
            payment.setStatus("PAID");
            payment.setTransactionRef(tx.getOrderCode());
            payment.setPaidAt(tx.getPaidAt() != null ? tx.getPaidAt() : LocalDateTime.now());
            payment.setCreatedAt(LocalDateTime.now());

            paymentRepository.save(payment);
        }

        cleanupSeatHolds(tx);
    }

    private void cleanupSeatHolds(PaymentTransaction tx) {
        seatHoldRepository.deleteByShowtimeIdAndUserId(tx.getShowtimeId(), tx.getUserId());
    }

    private BigDecimal nvl(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private final BookingComboService bookingComboService;
}
