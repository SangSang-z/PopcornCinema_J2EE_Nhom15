package com.example.PopcornCinema.dto;

public class BookingComboItemDto {
    private Long comboId;
    private Integer quantity;

    public BookingComboItemDto() {
    }

    public BookingComboItemDto(Long comboId, Integer quantity) {
        this.comboId = comboId;
        this.quantity = quantity;
    }

    public Long getComboId() { return comboId; }
    public Integer getQuantity() { return quantity; }

    public void setComboId(Long comboId) { this.comboId = comboId; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}