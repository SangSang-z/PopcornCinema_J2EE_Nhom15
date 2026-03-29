package com.example.PopcornCinema.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.*;

@Entity   
@Table(name = "users") 
public class User {

    @Id
    private String id;

    // các field khác
}