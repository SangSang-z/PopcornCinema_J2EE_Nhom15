package com.example.PopcornCinema.service;

import com.example.PopcornCinema.entity.*;
import com.example.PopcornCinema.repository.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class BookingService {

    private final BookingRepository bookingRepo;
    private final SeatHoldRepository seatHoldRepo;
    private final TicketRepository ticketRepo;

    public BookingService(BookingRepository bookingRepo,
                          SeatHoldRepository seatHoldRepo,
                          TicketRepository ticketRepo) {
        this.bookingRepo = bookingRepo;
        this.seatHoldRepo = seatHoldRepo;
        this.ticketRepo = ticketRepo;
    }

    /* ===============================
       TẠO BOOKING (SAU KHI HOLD GHẾ)
    =============================== */
    @Transactional
    public Booking createBooking(User user, Showtime showtime) {

        List<SeatHold> holds = seatHoldRepo.findByUser_Id(user.getId());

        if (holds.isEmpty()) {
            throw new RuntimeException("Bạn chưa chọn ghế");
        }

        double total = holds.stream()
                .mapToDouble(h -> showtime.getBasePrice() + h.getSeat().getExtraPrice())
                .sum();

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setShowtime(showtime);

        booking.setBookingCode("BK-" + UUID.randomUUID().toString().substring(0, 8));

        booking.setTotalAmount(total);
        booking.setStatus("PENDING_PAYMENT");

        bookingRepo.save(booking);

        return booking;
    }

    /* ===============================
       CONFIRM THANH TOÁN
    =============================== */
    @Transactional
    public void confirmBooking(Booking booking) {

        booking.setStatus("PAID");
        bookingRepo.save(booking);

        List<SeatHold> holds = seatHoldRepo.findByUser_Id(booking.getUser().getId());

        for (SeatHold hold : holds) {

            Ticket ticket = new Ticket();
            ticket.setBooking(booking);
            ticket.setShowtime(hold.getShowtime());
            ticket.setSeat(hold.getSeat());
            ticket.setUnitPrice(
                    hold.getShowtime().getBasePrice() + hold.getSeat().getExtraPrice()
            );

            ticketRepo.save(ticket);
        }

        seatHoldRepo.deleteAll(holds);
    }

    /* ===============================
       EXPIRE BOOKING (HẾT 5 PHÚT)
    =============================== */
    @Transactional
    public void expireBooking(Booking booking) {

        booking.setStatus("EXPIRED");
        bookingRepo.save(booking);

        // xóa ghế hold
        List<SeatHold> holds = seatHoldRepo.findByUser_Id(booking.getUser().getId());
        seatHoldRepo.deleteAll(holds);
    }
}