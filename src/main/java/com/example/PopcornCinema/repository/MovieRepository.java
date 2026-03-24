package com.example.PopcornCinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.PopcornCinema.entity.Movie;

public interface MovieRepository extends JpaRepository<Movie, Long> {

}