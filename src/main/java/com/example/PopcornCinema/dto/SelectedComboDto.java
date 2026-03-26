package com.example.PopcornCinema.dto;

import java.math.BigDecimal;

public class SelectedComboDto {
    private Long comboId;
    private String name;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;

    public SelectedComboDto() {
    }

    public SelectedComboDto(Long comboId, String name, BigDecimal price, Integer quantity, BigDecimal subtotal) {
        this.comboId = comboId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }

    public Long getComboId() { return comboId; }
    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }
    public Integer getQuantity() { return quantity; }
    public BigDecimal getSubtotal() { return subtotal; }

    public void setComboId(Long comboId) { this.comboId = comboId; }
    public void setName(String name) { this.name = name; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
}