package com.example.PopcornCinema.respository;

import com.example.PopcornCinema.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    @Query(value = """
        SELECT *
        FROM promotions p
        WHERE p.status = 'ACTIVE'
          AND NOW() BETWEEN p.start_date AND p.end_date
        ORDER BY p.id
    """, nativeQuery = true)
    List<Promotion> findActivePromotions();
}