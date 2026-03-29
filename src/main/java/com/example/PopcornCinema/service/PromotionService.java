package com.example.PopcornCinema.service;

import com.example.PopcornCinema.entity.*;
import com.example.PopcornCinema.respository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PromotionService {

    @Autowired
    private SavedPromotionRepository savedPromotionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PromotionRepository promotionRepository;

    public String savePromotion(String userId, Long promotionId) {

        if (savedPromotionRepository.existsByUser_IdAndPromotion_Id(userId, promotionId)) {
            return "Bạn đã lưu khuyến mãi này rồi!";
        }

        User user = userRepository.findById(userId).orElse(null);
        Promotion promotion = promotionRepository.findById(promotionId).orElse(null);

        if (user == null || promotion == null) {
            return "User hoặc Promotion không tồn tại!";
        }

        SavedPromotion saved = new SavedPromotion();
        saved.setUser(user);
        saved.setPromotion(promotion);

        savedPromotionRepository.save(saved);

        return "Lưu thành công!";
    }
}