package com.example.PopcornCinema.dto;

import java.math.BigDecimal;

public class SeatItemDto {
    private Long seatId;
    private String seatRow;
    private Integer seatNumber;
    private String seatType;
    private BigDecimal extraPrice;
    private BigDecimal finalPrice;
    private boolean sold;
    private boolean held;

    public SeatItemDto() {
    }

    public SeatItemDto(Long seatId, String seatRow, Integer seatNumber, String seatType,
                       BigDecimal extraPrice, BigDecimal finalPrice, boolean sold, boolean held) {
        this.seatId = seatId;
        this.seatRow = seatRow;
        this.seatNumber = seatNumber;
        this.seatType = seatType;
        this.extraPrice = extraPrice;
        this.finalPrice = finalPrice;
        this.sold = sold;
        this.held = held;
    }

    public Long getSeatId() {
        return seatId;
    }

    public String getSeatRow() {
        return seatRow;
    }

    public Integer getSeatNumber() {
        return seatNumber;
    }

    public String getSeatType() {
        return seatType;
    }

    public BigDecimal getExtraPrice() {
        return extraPrice;
    }

    public BigDecimal getFinalPrice() {
        return finalPrice;
    }

    public boolean isSold() {
        return sold;
    }

    public boolean isHeld() {
        return held;
    }

    public void setSeatId(Long seatId) {
        this.seatId = seatId;
    }

    public void setSeatRow(String seatRow) {
        this.seatRow = seatRow;
    }

    public void setSeatNumber(Integer seatNumber) {
        this.seatNumber = seatNumber;
    }

    public void setSeatType(String seatType) {
        this.seatType = seatType;
    }

    public void setExtraPrice(BigDecimal extraPrice) {
        this.extraPrice = extraPrice;
    }

    public void setFinalPrice(BigDecimal finalPrice) {
        this.finalPrice = finalPrice;
    }

    public void setSold(boolean sold) {
        this.sold = sold;
    }

    public void setHeld(boolean held) {
        this.held = held;
    }
}