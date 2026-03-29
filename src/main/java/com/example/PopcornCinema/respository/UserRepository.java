package com.example.PopcornCinema.respository;

import com.example.PopcornCinema.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}