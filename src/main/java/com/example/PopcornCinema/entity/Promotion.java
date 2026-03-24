package com.example.PopcornCinema.entity;

import jakarta.persistence.*;

@Entity
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    private int discountPercent;

    private double minOrderValue;

    private boolean active;

    public Promotion(){}

    public Long getId(){
        return id;
    }

    public String getCode(){
        return code;
    }

    public void setCode(String code){
        this.code = code;
    }

    public int getDiscountPercent(){
        return discountPercent;
    }

    public void setDiscountPercent(int discountPercent){
        this.discountPercent = discountPercent;
    }

    public double getMinOrderValue(){
        return minOrderValue;
    }

    public void setMinOrderValue(double minOrderValue){
        this.minOrderValue = minOrderValue;
    }

    public boolean isActive(){
        return active;
    }

    public void setActive(boolean active){
        this.active = active;
    }
}