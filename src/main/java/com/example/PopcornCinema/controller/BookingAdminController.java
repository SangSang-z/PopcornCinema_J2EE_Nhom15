package com.example.PopcornCinema.controller;

import com.example.PopcornCinema.dto.AdminPaymentRowDto;
import com.example.PopcornCinema.entity.Booking;
import com.example.PopcornCinema.entity.PaymentTransaction;
import com.example.PopcornCinema.entity.Showtime;
import com.example.PopcornCinema.entity.Ticket;
import com.example.PopcornCinema.entity.User;
import com.example.PopcornCinema.repository.BookingRepository;
import com.example.PopcornCinema.repository.PaymentTransactionRepository;
import com.example.PopcornCinema.repository.SeatHoldRepository;
import com.example.PopcornCinema.repository.ShowtimeRepository;
import com.example.PopcornCinema.repository.TicketRepository;
import com.example.PopcornCinema.repository.UserRepository;
import com.example.PopcornCinema.service.PaymentTransactionService;
import com.example.PopcornCinema.service.impl.PaymentTransactionServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/bookings")
public class BookingAdminController {

    private final PaymentTransactionRepository paymentTransactionRepository;
    private final UserRepository userRepository;
    private final ShowtimeRepository showtimeRepository;
    private final BookingRepository bookingRepository;
    private final TicketRepository ticketRepository;
    private final SeatHoldRepository seatHoldRepository;
    private final PaymentTransactionService paymentTransactionService;

    public BookingAdminController(PaymentTransactionRepository paymentTransactionRepository,
                                  UserRepository userRepository,
                                  ShowtimeRepository showtimeRepository,
                                  BookingRepository bookingRepository,
                                  TicketRepository ticketRepository,
                                  SeatHoldRepository seatHoldRepository,
                                  PaymentTransactionService paymentTransactionService) {
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.userRepository = userRepository;
        this.showtimeRepository = showtimeRepository;
        this.bookingRepository = bookingRepository;
        this.ticketRepository = ticketRepository;
        this.seatHoldRepository = seatHoldRepository;
        this.paymentTransactionService = paymentTransactionService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String keyword, Model model) {
        String normalizedKeyword = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);

        List<AdminPaymentRowDto> rows = paymentTransactionRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toRowDto)
                .filter(row -> matchesKeyword(row, normalizedKeyword))
                .sorted(Comparator.comparing(AdminPaymentRowDto::getCreatedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());

        model.addAttribute("rows", rows);
        model.addAttribute("keyword", keyword);
        model.addAttribute("activePage", "bookings");
        return "admin/bookings/list";
    }

    @PostMapping("/{orderCode}/confirm")
    public String confirm(@PathVariable String orderCode) {
        paymentTransactionService.confirmByAdmin(orderCode);
        return "redirect:/admin/bookings";
    }

    @PostMapping("/{orderCode}/reject")
    public String reject(@PathVariable String orderCode) {
        paymentTransactionService.rejectByAdmin(orderCode);
        return "redirect:/admin/bookings";
    }

    private boolean matchesKeyword(AdminPaymentRowDto row, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return true;
        }

        return contains(row.getOrderCode(), keyword)
                || contains(row.getUserName(), keyword)
                || contains(row.getUserEmail(), keyword)
                || contains(row.getUserPhone(), keyword)
                || contains(row.getMovieTitle(), keyword)
                || contains(row.getSeatsText(), keyword)
                || contains(row.getTransactionStatus(), keyword);
    }

    private boolean contains(String text, String keyword) {
        return text != null && text.toLowerCase(Locale.ROOT).contains(keyword);
    }

    private AdminPaymentRowDto toRowDto(PaymentTransaction tx) {
        User user = userRepository.findById(tx.getUserId()).orElse(null);
        Showtime showtime = showtimeRepository.findById(tx.getShowtimeId()).orElse(null);
        Booking booking = tx.getBookingId() != null ? bookingRepository.findById(tx.getBookingId()).orElse(null) : null;

        String seatsText;
        if (booking != null) {
            List<Ticket> tickets = ticketRepository.findAllByBookingId(booking.getId());
            seatsText = tickets.stream()
                    .filter(t -> t.getSeat() != null)
                    .map(t -> t.getSeat().getSeatRow() + t.getSeat().getSeatNumber())
                    .collect(Collectors.joining(", "));
        } else {
            seatsText = seatHoldRepository.findHeldSeatSummaryByShowtimeIdAndUserId(tx.getShowtimeId(), tx.getUserId())
                    .stream()
                    .map(seat -> seat.getSeatRow() + seat.getSeatNumber())
                    .collect(Collectors.joining(", "));
        }

        if (seatsText == null || seatsText.isBlank()) {
            seatsText = "Chưa có ghế";
        }

        AdminPaymentRowDto dto = new AdminPaymentRowDto();
        dto.setId(tx.getId());
        dto.setOrderCode(tx.getOrderCode());
        dto.setUserName(user != null ? user.getFullName() : "N/A");
        dto.setUserEmail(user != null ? user.getEmail() : "");
        dto.setUserPhone(user != null ? user.getPhone() : "");
        dto.setMovieTitle(showtime != null && showtime.getMovie() != null ? showtime.getMovie().getTitle() : "N/A");
        dto.setShowtimeStart(showtime != null ? showtime.getStartTime() : null);
        dto.setSeatsText(seatsText);
        dto.setAmount(tx.getAmount());
        dto.setTransactionStatus(tx.getStatus());
        dto.setBookingCode(booking != null ? booking.getBookingCode() : "");
        dto.setBookingStatus(booking != null ? booking.getStatus() : "");
        dto.setCreatedAt(tx.getCreatedAt());
        dto.setCanConfirm(PaymentTransactionServiceImpl.STATUS_PENDING_CONFIRMATION.equals(tx.getStatus()));
        dto.setCanReject(PaymentTransactionServiceImpl.STATUS_PENDING_CONFIRMATION.equals(tx.getStatus()));
        return dto;
    }
}