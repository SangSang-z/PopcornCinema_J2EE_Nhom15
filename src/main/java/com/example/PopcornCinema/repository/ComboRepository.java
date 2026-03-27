package com.example.PopcornCinema.repository;

import com.example.PopcornCinema.entity.Combo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComboRepository extends JpaRepository<Combo, Long> {
    List<Combo> findByStatusOrderByIdAsc(String status);
}