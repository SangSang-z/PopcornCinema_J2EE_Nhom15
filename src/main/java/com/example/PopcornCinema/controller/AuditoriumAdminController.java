package com.example.PopcornCinema.controller;

import com.example.PopcornCinema.entity.Auditorium;
import com.example.PopcornCinema.entity.Seat;
import com.example.PopcornCinema.repository.AuditoriumRepository;
import com.example.PopcornCinema.repository.SeatRepository;
import com.example.PopcornCinema.repository.TicketRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.stream.Collectors;

import java.util.*;

@Controller
@RequestMapping("/admin/auditoriums")
public class AuditoriumAdminController {

    private final AuditoriumRepository auditoriumRepository;
    private final SeatRepository seatRepository;
    private final TicketRepository ticketRepository;

    // CONSTRUCTOR 
    public AuditoriumAdminController(AuditoriumRepository auditoriumRepository,
                                     SeatRepository seatRepository,
                                     TicketRepository ticketRepository) {
        this.auditoriumRepository = auditoriumRepository;
        this.seatRepository = seatRepository;
        this.ticketRepository = ticketRepository;
    }

    // ===============================
    // Danh sách phòng
    // ===============================
    @GetMapping
    public String listRooms(Model model) {

        List<Auditorium> rooms = auditoriumRepository.findAllWithCinema();

        model.addAttribute("rooms", rooms);
        model.addAttribute("activePage", "rooms");

        return "admin/auditoriums/list";
    }

    // ===============================
    // Xem ghế theo phòng
    // ===============================
    @GetMapping("/{auditoriumId}/seats")
    public String viewSeats(@PathVariable Long auditoriumId, Model model) {

        Auditorium auditorium = auditoriumRepository.findById(auditoriumId)
                .orElseThrow(() -> new RuntimeException("Phòng không tồn tại"));

        List<Seat> seats = seatRepository.findByAuditoriumId(auditoriumId);

        Map<String, List<Seat>> seatMap = seats.stream()
                .sorted(Comparator.comparing(Seat::getSeatRow)
                        .thenComparing(Seat::getSeatNumber))
                .collect(Collectors.groupingBy(
                        Seat::getSeatRow,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        List<Long> bookedSeatIds = ticketRepository.findBookedSeatIds(1L);

        model.addAttribute("auditorium", auditorium);
        model.addAttribute("seatMap", seatMap);
        model.addAttribute("bookedSeatIds", bookedSeatIds);

        return "admin/seats/list";
    }
}
