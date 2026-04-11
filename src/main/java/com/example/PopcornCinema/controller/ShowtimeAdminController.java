package com.example.PopcornCinema.controller;

import com.example.PopcornCinema.entity.*;
import com.example.PopcornCinema.repository.*;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/admin/showtimes")
public class ShowtimeAdminController {

    private final ShowtimeRepository showtimeRepo;
    private final MovieRepository movieRepo;
    private final AuditoriumRepository auditoriumRepo;

    public ShowtimeAdminController(
            ShowtimeRepository showtimeRepo,
            MovieRepository movieRepo,
            AuditoriumRepository auditoriumRepo) {

        this.showtimeRepo = showtimeRepo;
        this.movieRepo = movieRepo;
        this.auditoriumRepo = auditoriumRepo;
    }

    // LIST
    @GetMapping
    public String list(Model model){
        model.addAttribute("showtimes", showtimeRepo.findAll(Sort.by(Sort.Direction.DESC, "id")));
        model.addAttribute("activePage","showtimes");
        return "admin/showtimes/list";
    }

    // CREATE FORM
    @GetMapping("/create")
    public String createForm(Model model){
        model.addAttribute("movies", movieRepo.findAll());
        model.addAttribute("auditoriums", auditoriumRepo.findAll()); // 🔥 FIX
        return "admin/showtimes/create";
    }

    // SAVE
    @PostMapping("/save")
    public String save(
            @RequestParam Long movieId,
            @RequestParam Long auditoriumId,
            @RequestParam String startTime,
            @RequestParam Double basePrice){

        Movie movie = movieRepo.findById(movieId).orElseThrow();
        Auditorium auditorium = auditoriumRepo.findById(auditoriumId).orElseThrow();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime start = LocalDateTime.parse(startTime, formatter);
        LocalDateTime end = start.plusMinutes(movie.getDurationMinutes());

        // CHECK TRÙNG
        List<Showtime> conflicts = showtimeRepo.findConflictingShowtimes(
                auditoriumId, start, end);

        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Phòng đã có suất chiếu!");
        }

        Showtime s = new Showtime();
        s.setMovie(movie);
        s.setAuditorium(auditorium);
        s.setStartTime(start);
        s.setEndTime(end);
        s.setBasePrice(basePrice);
        s.setStatus("OPEN");

        showtimeRepo.save(s);

        return "redirect:/admin/showtimes";
    }

    // EDIT
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model){

        Showtime showtime = showtimeRepo.findById(id).orElseThrow();

        model.addAttribute("showtime", showtime);
        model.addAttribute("movies", movieRepo.findAll());
        model.addAttribute("auditoriums", auditoriumRepo.findAll()); // 🔥 FIX

        return "admin/showtimes/edit";
    }

    // UPDATE
    @PostMapping("/update")
    public String update(
            @RequestParam Long id,
            @RequestParam Long movieId,
            @RequestParam Long auditoriumId,
            @RequestParam String startTime,
            @RequestParam Double basePrice){

        Showtime showtime = showtimeRepo.findById(id).orElseThrow();
        Movie movie = movieRepo.findById(movieId).orElseThrow();
        Auditorium auditorium = auditoriumRepo.findById(auditoriumId).orElseThrow();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime start = LocalDateTime.parse(startTime, formatter);
        LocalDateTime end = start.plusMinutes(movie.getDurationMinutes());

        List<Showtime> conflicts = showtimeRepo.findConflictingShowtimes(
                auditoriumId, start, end);

        conflicts.removeIf(s -> s.getId().equals(id));

        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Phòng đã có suất chiếu!");
        }

        showtime.setMovie(movie);
        showtime.setAuditorium(auditorium);
        showtime.setStartTime(start);
        showtime.setEndTime(end);
        showtime.setBasePrice(basePrice);

        showtimeRepo.save(showtime);

        return "redirect:/admin/showtimes";
    }

    // DELETE
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id){
        showtimeRepo.deleteById(id);
        return "redirect:/admin/showtimes";
    }
}
