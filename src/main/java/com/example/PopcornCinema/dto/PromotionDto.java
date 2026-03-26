package com.example.PopcornCinema.dto;

import java.math.BigDecimal;

public class PromotionDto {
    private Long id;
    private String title;
    private String description;
    private BigDecimal discountPercent;
    private BigDecimal discountAmount;

    public PromotionDto() {
    }

    public PromotionDto(Long id, String title, String description,
                        BigDecimal discountPercent, BigDecimal discountAmount) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.discountPercent = discountPercent;
        this.discountAmount = discountAmount;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public BigDecimal getDiscountPercent() { return discountPercent; }
    public BigDecimal getDiscountAmount() { return discountAmount; }

    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setDiscountPercent(BigDecimal discountPercent) { this.discountPercent = discountPercent; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
}