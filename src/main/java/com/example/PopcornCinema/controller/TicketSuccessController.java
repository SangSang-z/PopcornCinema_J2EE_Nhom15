package com.example.PopcornCinema.controller;

import com.example.PopcornCinema.dto.TicketSuccessResponse;
import com.example.PopcornCinema.service.TicketSuccessService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "*")
public class TicketSuccessController {

    private final TicketSuccessService ticketSuccessService;

    public TicketSuccessController(TicketSuccessService ticketSuccessService) {
        this.ticketSuccessService = ticketSuccessService;
    }

    @GetMapping("/success-info")
    public ResponseEntity<TicketSuccessResponse> getSuccessInfo(@RequestParam("orderCode") String orderCode) {
        return ResponseEntity.ok(ticketSuccessService.getSuccessInfo(orderCode));
    }
}