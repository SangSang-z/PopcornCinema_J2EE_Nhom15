package com.example.PopcornCinema.repository;

import com.example.PopcornCinema.entity.Auditorium;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditoriumRepository extends JpaRepository<Auditorium, Long> {
}