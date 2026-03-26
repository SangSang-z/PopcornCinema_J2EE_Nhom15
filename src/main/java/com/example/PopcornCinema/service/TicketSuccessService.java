package com.example.PopcornCinema.service;

import com.example.PopcornCinema.dto.TicketSuccessResponse;

public interface TicketSuccessService {
    TicketSuccessResponse getSuccessInfo(String orderCode);
}