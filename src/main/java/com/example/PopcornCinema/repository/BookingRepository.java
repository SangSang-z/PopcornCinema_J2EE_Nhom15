package com.example.PopcornCinema.repository;

import com.example.PopcornCinema.entity.Booking;
import com.example.PopcornCinema.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUser(User user);

    List<Booking> findByUser_EmailContainingOrUser_PhoneContaining(String email, String phone);
}