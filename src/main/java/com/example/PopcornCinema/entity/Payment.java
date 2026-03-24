package com.example.PopcornCinema.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    private String paymentMethod;

    private double amount;

    private String status; // PENDING, SUCCESS, FAILED

    private LocalDateTime paidAt;
}