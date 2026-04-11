package com.example.PopcornCinema.repository;

import com.example.PopcornCinema.entity.Auditorium;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AuditoriumRepository extends JpaRepository<Auditorium, Long> {
    @Query("select a from Auditorium a left join fetch a.cinema")
    List<Auditorium> findAllWithCinema();
}
