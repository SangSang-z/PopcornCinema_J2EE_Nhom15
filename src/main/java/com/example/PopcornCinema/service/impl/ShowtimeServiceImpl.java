package com.example.PopcornCinema.service.impl;

import com.example.PopcornCinema.dto.SeatItemDto;
import com.example.PopcornCinema.dto.SeatMapResponse;
import com.example.PopcornCinema.repository.ShowtimeRepository;
import com.example.PopcornCinema.repository.TicketRepository;
import com.example.PopcornCinema.repository.projection.SeatProjection;
import com.example.PopcornCinema.repository.projection.ShowtimeSeatMapProjection;
import com.example.PopcornCinema.service.SeatHoldService;
import com.example.PopcornCinema.service.ShowtimeService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ShowtimeServiceImpl implements ShowtimeService {

    private final ShowtimeRepository showtimeRepository;
    private final SeatHoldService seatHoldService;
    private final TicketRepository ticketRepository;

    public ShowtimeServiceImpl(ShowtimeRepository showtimeRepository,
                               SeatHoldService seatHoldService,
                               TicketRepository ticketRepository) {
        this.showtimeRepository = showtimeRepository;
        this.seatHoldService = seatHoldService;
        this.ticketRepository = ticketRepository;
    }

    @Override
    public SeatMapResponse getSeatMap(Long showtimeId, Long userId) {
        ShowtimeSeatMapProjection info = showtimeRepository.findSeatMapInfoByShowtimeId(showtimeId);
        if (info == null) {
            throw new RuntimeException("Không tìm thấy suất chiếu");
        }

        List<SeatProjection> seats = showtimeRepository.findSeatsByShowtimeId(showtimeId);
        Set<Long> heldSeatIds = seatHoldService.getHeldSeatIdsByOtherUsers(showtimeId, userId);
        Set<Long> soldSeatIds = new HashSet<>(ticketRepository.findSoldSeatIdsByShowtimeId(showtimeId));

        SeatMapResponse response = new SeatMapResponse();
        response.setShowtimeId(info.getShowtimeId());
        response.setMovieTitle(info.getMovieTitle());
        response.setPosterUrl(info.getPosterUrl());
        response.setAgeRating(info.getAgeRating());
        response.setCinemaName(info.getCinemaName());
        response.setAuditoriumName(info.getAuditoriumName());
        response.setStartTime(info.getStartTime());
        response.setEndTime(info.getEndTime());
        response.setBasePrice(info.getBasePrice());

        response.setSeats(
                seats.stream().map(seat -> {
                    boolean sold = soldSeatIds.contains(seat.getSeatId());
                    boolean held = !sold && heldSeatIds.contains(seat.getSeatId());

                    return new SeatItemDto(
                            seat.getSeatId(),
                            seat.getSeatRow(),
                            seat.getSeatNumber(),
                            seat.getSeatType(),
                            seat.getExtraPrice(),
                            info.getBasePrice().add(
                                    seat.getExtraPrice() == null ? BigDecimal.ZERO : seat.getExtraPrice()
                            ),
                            sold,
                            held
                    );
                }).toList()
        );

        return response;
    }
}