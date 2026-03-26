package com.example.PopcornCinema.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BookingPageController {

    @GetMapping("/seats")
    public String seatsPage() {
        return "booking/seats";
    }

    @GetMapping("/payment")
    public String paymentPage() {
        return "booking/payment";
}
    @GetMapping("/checkout")
    public String checkoutPage() {
        return "booking/checkout";
    }

    @GetMapping("/checkout-qr")
    public String checkoutQrPage() {
        return "booking/checkout-qr";
    }

    @GetMapping("/payment-failed")
    public String paymentFailedPage() {
        return "booking/payment-failed";
}

    @GetMapping("/ticket-success")
    public String ticketSuccessPage() {
        return "booking/ticket-success";
    }
}