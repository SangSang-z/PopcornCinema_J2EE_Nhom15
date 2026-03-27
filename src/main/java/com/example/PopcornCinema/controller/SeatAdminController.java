package com.example.PopcornCinema.controller;

import com.example.PopcornCinema.entity.Seat;
import com.example.PopcornCinema.repository.SeatRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/seats")
public class SeatAdminController {

    private final SeatRepository seatRepo;

    public SeatAdminController(SeatRepository seatRepo){
        this.seatRepo = seatRepo;
    }

    // ================== LIST ALL SEATS ==================
    @GetMapping
    public String listAll(Model model){
        model.addAttribute("seats", seatRepo.findAll());
        model.addAttribute("activePage","seats");
        return "admin/seats/list";
    }

}