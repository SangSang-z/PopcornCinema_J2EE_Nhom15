package com.example.PopcornCinema.repository;

import com.example.PopcornCinema.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    Promotion findByCode(String code);

    @Query(value = """
        SELECT *
        FROM promotions p
        WHERE p.status = 'ACTIVE'
        ORDER BY p.id
    """, nativeQuery = true)
    List<Promotion> findActivePromotions();
}
