package com.example.PopcornCinema.controller;

import com.example.PopcornCinema.entity.Booking;
import com.example.PopcornCinema.entity.User;
import com.example.PopcornCinema.repository.BookingRepository;
import com.example.PopcornCinema.repository.UserRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/users")
public class UserAdminController {

    private final UserRepository userRepo;
    private final BookingRepository bookingRepo;

    public UserAdminController(UserRepository userRepo,
                               BookingRepository bookingRepo){
        this.userRepo = userRepo;
        this.bookingRepo = bookingRepo;
    }

    /* ===============================
       LIST + SEARCH
    =============================== */
    @GetMapping
    public String list(@RequestParam(required = false) String keyword,
                       Model model){

        List<User> users;

        if(keyword != null && !keyword.trim().isEmpty()){
            users = userRepo.findByFullNameContainingOrEmailContaining(keyword, keyword);
        }else{
            users = userRepo.findAll();
        }

        model.addAttribute("users", users);
        model.addAttribute("keyword", keyword);
        model.addAttribute("activePage","users");

        return "admin/users/list";
    }

    /* ===============================
       TOGGLE USER (ACTIVE <-> LOCKED)
    =============================== */
    @PostMapping("/toggle/{id}")
    public String toggleUser(@PathVariable Long id){

        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        if ("ACTIVE".equals(user.getStatus())) {
            user.setStatus("LOCKED");
        } else {
            user.setStatus("ACTIVE");
        }

        userRepo.save(user);

        return "redirect:/admin/users";
    }

    /* ===============================
       USER DETAIL + BOOKINGS
    =============================== */
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model){

        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        List<Booking> bookings = bookingRepo.findByUser(user);

        // 🔥 TÍNH TỔNG TIỀN
        double totalSpent = bookings.stream()
                .filter(b -> "PAID".equals(b.getStatus()))
                .mapToDouble(Booking::getTotalAmount)
                .sum();

        model.addAttribute("user", user);
        model.addAttribute("bookings", bookings);
        model.addAttribute("totalSpent", totalSpent);
        model.addAttribute("activePage","users");

        return "admin/users/detail";
    }
}