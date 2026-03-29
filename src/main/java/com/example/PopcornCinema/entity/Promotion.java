package com.example.PopcornCinema.entity;

import jakarta.persistence.*;

@Entity   
@Table(name = "promotion") 
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // các field khác...
}