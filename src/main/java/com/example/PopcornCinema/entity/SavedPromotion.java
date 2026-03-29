package com.example.PopcornCinema.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "saved_promotion",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "promotion_id"})
)
public class SavedPromotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;

    private LocalDateTime createdAt = LocalDateTime.now();

    // getter setter
    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Promotion getPromotion() {
        return promotion;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setPromotion(Promotion promotion) {
        this.promotion = promotion;
    }
}