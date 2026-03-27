package com.example.PopcornCinema.repository;

import com.example.PopcornCinema.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    List<User> findByFullNameContainingOrEmailContaining(String name, String email);
}