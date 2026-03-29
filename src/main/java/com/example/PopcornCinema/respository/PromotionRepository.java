package com.example.PopcornCinema.respository;

import com.example.PopcornCinema.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {
}