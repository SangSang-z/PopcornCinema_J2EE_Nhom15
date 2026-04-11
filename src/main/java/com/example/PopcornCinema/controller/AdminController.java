package com.example.PopcornCinema.controller;

import com.example.PopcornCinema.entity.User;
import com.example.PopcornCinema.entity.Booking;
import com.example.PopcornCinema.entity.Movie;
import com.example.PopcornCinema.repository.MovieRepository;
import com.example.PopcornCinema.repository.BookingRepository;
import com.example.PopcornCinema.repository.TicketRepository;
import com.example.PopcornCinema.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    /* ===============================
       CHECK ADMIN
    =============================== */
    private boolean isAdmin(HttpSession session){
        User user = (User) session.getAttribute("user");
        return user != null && "ADMIN".equals(user.getRole());
    }

    /* ===============================
       /admin
    =============================== */
    @GetMapping
    public String adminHome(HttpSession session) {

        if(!isAdmin(session)){
            return "redirect:/login";
        }

        return "redirect:/admin/dashboard";
    }

    /* ===============================
       /admin/dashboard
    =============================== */
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model){

        if(!isAdmin(session)){
            return "redirect:/login";
        }

        // active menu
        model.addAttribute("activePage","dashboard");

        // ===============================
        // REAL DATA
        // ===============================
        final String paidStatus = "PAID";
        final String confirmedStatus = "CONFIRMED";
        BigDecimal revenuePaid = bookingRepository.sumTotalAmountByStatus(paidStatus);
        BigDecimal revenueConfirmed = bookingRepository.sumTotalAmountByStatus(confirmedStatus);
        if (revenuePaid == null) {
            revenuePaid = BigDecimal.ZERO;
        }
        if (revenueConfirmed == null) {
            revenueConfirmed = BigDecimal.ZERO;
        }
        BigDecimal totalRevenue = revenuePaid.add(revenueConfirmed);

        long totalTickets = ticketRepository.countSoldTickets();
        long totalOrders = bookingRepository.countByStatusIgnoreCase(paidStatus)
                + bookingRepository.countByStatusIgnoreCase(confirmedStatus);

        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(6);
        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = today.plusDays(1).atStartOfDay();

        long newUsers = userRepository.countByRoleIgnoreCaseAndCreatedAtBetween(
                "CUSTOMER",
                startTime,
                endTime
        );

        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalTickets", totalTickets);
        model.addAttribute("newUsers", newUsers);
        model.addAttribute("totalOrders", totalOrders);

        // ===============================
        // REVENUE CHART (7 DAYS)
        // ===============================
        List<Booking> recentBookings = new ArrayList<>();
        recentBookings.addAll(
                bookingRepository.findByStatusIgnoreCaseAndCreatedAtBetween(
                        paidStatus,
                        startTime,
                        endTime
                )
        );
        recentBookings.addAll(
                bookingRepository.findByStatusIgnoreCaseAndCreatedAtBetween(
                        confirmedStatus,
                        startTime,
                        endTime
                )
        );

        Map<LocalDate, BigDecimal> revenueByDate = new HashMap<>();
        for (Booking booking : recentBookings) {
            if (booking.getCreatedAt() == null) {
                continue;
            }
            LocalDate date = booking.getCreatedAt().toLocalDate();
            BigDecimal amount = booking.getTotalAmount() != null
                    ? booking.getTotalAmount()
                    : BigDecimal.ZERO;
            revenueByDate.merge(date, amount, BigDecimal::add);
        }

        DateTimeFormatter labelFormatter = DateTimeFormatter.ofPattern("dd/MM");
        List<String> revenueLabels = new ArrayList<>();
        List<Double> revenueValues = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            LocalDate date = startDate.plusDays(i);
            revenueLabels.add(date.format(labelFormatter));
            BigDecimal amount = revenueByDate.getOrDefault(date, BigDecimal.ZERO);
            revenueValues.add(amount.doubleValue());
        }

        model.addAttribute("revenueLabels", revenueLabels);
        model.addAttribute("revenueValues", revenueValues);

        // ===============================
        // MOVIES
        // ===============================
        List<Movie> topMovies =
                movieRepository.findTopMoviesByTicketSales(PageRequest.of(0, 5));
        if (topMovies.isEmpty()) {
            topMovies = movieRepository.findByStatusOrderByReleaseDateDescIdDesc("NOW_SHOWING");
        }

        model.addAttribute("topMovies", topMovies);
        model.addAttribute("nowShowingMovies",
                movieRepository.findByStatusOrderByReleaseDateDescIdDesc("NOW_SHOWING"));

        return "admin/dashboard";
    }
}
