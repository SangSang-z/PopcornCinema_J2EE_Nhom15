package com.example.PopcornCinema.controller;

import com.example.PopcornCinema.entity.Booking;
import com.example.PopcornCinema.repository.BookingRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/bookings")
public class BookingAdminController {

    private final BookingRepository bookingRepo;

    public BookingAdminController(BookingRepository bookingRepo) {
        this.bookingRepo = bookingRepo;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String keyword,
                       Model model) {

        List<Booking> bookings;

        if(keyword != null && !keyword.isEmpty()){
            bookings = bookingRepo
                    .findByUser_EmailContainingOrUser_PhoneContaining(keyword, keyword);
        }else{
            bookings = bookingRepo.findAll();
        }

        model.addAttribute("bookings", bookings);
        model.addAttribute("keyword", keyword);
        model.addAttribute("activePage","bookings");

        return "admin/bookings/list";
    }
}