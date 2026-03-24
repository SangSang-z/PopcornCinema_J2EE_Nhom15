package com.example.PopcornCinema.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "combo")
public class Combo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name = ""; 

    @Column(columnDefinition = "TEXT")
    private String description = "";

    @Column(nullable = false)
    private Double price = 0.0;

    private String image = "";

    private String status = "ACTIVE"; 


    // ================= GETTER / SETTER =================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}