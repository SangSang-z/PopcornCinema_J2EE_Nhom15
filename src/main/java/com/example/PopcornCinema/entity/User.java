package com.example.PopcornCinema.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name")
    private String fullName;

    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    private String phone;

    private String role; // ADMIN / CUSTOMER

    @Column(name = "status")
    private String status; // ACTIVE / LOCKED

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public User(){}

    /* ===============================
       AUTO SET
    =============================== */
    @PrePersist
    public void prePersist(){
        this.createdAt = LocalDateTime.now();

        if(this.role == null){
            this.role = "CUSTOMER";
        }

        if(this.status == null){
            this.status = "ACTIVE";
        }
    }

    /* ===============================
       GETTER SETTER
    =============================== */

    public Long getId(){
        return id;
    }

    public String getFullName(){
        return fullName;
    }

    public void setFullName(String fullName){
        this.fullName = fullName;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }


    public String getPasswordHash(){
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash){
        this.passwordHash = passwordHash;
    }

    public String getPhone(){
        return phone;
    }

    public void setPhone(String phone){
        this.phone = phone;
    }

    public String getRole(){
        return role;
    }

    public void setRole(String role){
        this.role = role;
    }

    public String getStatus(){
        return status;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public LocalDateTime getCreatedAt(){
        return createdAt;
    }
}