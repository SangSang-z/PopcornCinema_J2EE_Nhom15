package com.example.PopcornCinema.controller;

import com.example.PopcornCinema.dto.UserBookingHistoryDto;
import com.example.PopcornCinema.entity.Booking;
import com.example.PopcornCinema.entity.Payment;
import com.example.PopcornCinema.entity.Showtime;
import com.example.PopcornCinema.entity.Ticket;
import com.example.PopcornCinema.entity.User;
import com.example.PopcornCinema.repository.BookingRepository;
import com.example.PopcornCinema.repository.PaymentRepository;
import com.example.PopcornCinema.repository.ShowtimeRepository;
import com.example.PopcornCinema.repository.TicketRepository;
import com.example.PopcornCinema.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/profile")
public class UserPageController {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final TicketRepository ticketRepository;
    private final PaymentRepository paymentRepository;
    private final ShowtimeRepository showtimeRepository;

    public UserPageController(UserRepository userRepository,
                              BookingRepository bookingRepository,
                              TicketRepository ticketRepository,
                              PaymentRepository paymentRepository,
                              ShowtimeRepository showtimeRepository) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.ticketRepository = ticketRepository;
        this.paymentRepository = paymentRepository;
        this.showtimeRepository = showtimeRepository;
    }

    @GetMapping
    public String profile(HttpSession session, Model model) {
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) {
            return "redirect:/login";
        }

        User user = userRepository.findById(sessionUser.getId()).orElse(sessionUser);
        model.addAttribute("user", user);
        model.addAttribute("orderHistory", buildOrderHistory(user.getId()));
        return "user/profile";
    }

    @PostMapping("/update")
    public String updateProfile(@RequestParam String fullName,
                                @RequestParam(required = false) String phone,
                                HttpSession session,
                                Model model) {
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) {
            return "redirect:/login";
        }

        if (fullName == null || fullName.trim().isEmpty()) {
            model.addAttribute("error", "Họ tên không được để trống");
            model.addAttribute("user", sessionUser);
            return "user/profile";
        }

        User user = userRepository.findById(sessionUser.getId()).orElse(sessionUser);
        user.setFullName(fullName.trim());
        user.setPhone(phone != null ? phone.trim() : null);
        userRepository.save(user);
        session.setAttribute("user", user);

        model.addAttribute("user", user);
        model.addAttribute("orderHistory", buildOrderHistory(user.getId()));
        model.addAttribute("success", "Cập nhật thông tin thành công");
        return "user/profile";
    }

    private java.util.List<UserBookingHistoryDto> buildOrderHistory(Long userId) {
        if (userId == null) {
            return java.util.List.of();
        }

        return bookingRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toHistoryDto)
                .toList();
    }

    private UserBookingHistoryDto toHistoryDto(Booking booking) {
        UserBookingHistoryDto dto = new UserBookingHistoryDto();
        dto.setBookingCode(booking.getBookingCode());
        dto.setTotalAmount(booking.getTotalAmount());
        dto.setBookingStatus(booking.getStatus());
        dto.setCreatedAt(booking.getCreatedAt());

        Showtime showtime = booking.getShowtime();
        if (showtime == null && booking.getShowtimeId() != null) {
            showtime = showtimeRepository.findById(booking.getShowtimeId()).orElse(null);
        }
        if (showtime != null) {
            dto.setShowtimeStart(showtime.getStartTime());
            if (showtime.getMovie() != null) {
                dto.setMovieTitle(showtime.getMovie().getTitle());
            }
            if (showtime.getAuditorium() != null) {
                dto.setAuditoriumName(showtime.getAuditorium().getName());
                if (showtime.getAuditorium().getCinema() != null) {
                    dto.setCinemaName(showtime.getAuditorium().getCinema().getName());
                }
            }
        }

        java.util.List<Ticket> tickets = ticketRepository.findAllByBookingId(booking.getId());
        String seatsText = tickets.stream()
                .filter(t -> t.getSeat() != null)
                .map(t -> t.getSeat().getSeatRow() + t.getSeat().getSeatNumber())
                .collect(java.util.stream.Collectors.joining(", "));
        dto.setSeatsText(seatsText == null || seatsText.isBlank() ? "Chưa có ghế" : seatsText);

        Payment payment = paymentRepository.findByBookingId(booking.getId()).orElse(null);
        if (payment != null) {
            dto.setPaymentStatus(payment.getStatus());
            dto.setPaymentMethod(payment.getPaymentMethod());
        } else {
            dto.setPaymentStatus("PENDING");
            dto.setPaymentMethod("QR");
        }

        return dto;
    }
}
