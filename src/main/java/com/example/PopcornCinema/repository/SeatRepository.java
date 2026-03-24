package com.example.PopcornCinema.repository;

import com.example.PopcornCinema.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByAuditoriumId(Long auditoriumId);

}