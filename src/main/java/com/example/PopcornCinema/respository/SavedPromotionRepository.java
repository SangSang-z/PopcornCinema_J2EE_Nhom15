package com.example.PopcornCinema.respository;
import com.example.PopcornCinema.entity.SavedPromotion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SavedPromotionRepository extends JpaRepository<SavedPromotion, Long> {

    boolean existsByUser_IdAndPromotion_Id(String userId, Long promotionId);
}