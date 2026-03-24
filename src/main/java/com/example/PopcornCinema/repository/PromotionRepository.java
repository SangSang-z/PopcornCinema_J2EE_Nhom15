package com.example.PopcornCinema.repository;

import com.example.PopcornCinema.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    Promotion findByCode(String code);

}