package com.example.PopcornCinema.controller;

import com.example.PopcornCinema.entity.Movie;
import com.example.PopcornCinema.repository.MovieRepository;
import com.example.PopcornCinema.repository.ShowtimeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/admin/movies")
public class MovieAdminController {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ShowtimeRepository showtimeRepository;
    private static final String UPLOAD_DIR = "uploads/movies/";

    /* ===============================
       LIST
    =============================== */
    @GetMapping
    public String list(Model model){
        model.addAttribute("movies", movieRepository.findAll());
        model.addAttribute("activePage","movies");
        return "admin/movies/list";
    }

    /* ===============================
       CREATE PAGE
    =============================== */
    @GetMapping("/create")
    public String create(Model model){
        model.addAttribute("movie", new Movie());
        model.addAttribute("activePage","movies");
        return "admin/movies/create";
    }

    /* ===============================
       SAVE (CREATE)
    =============================== */
    @PostMapping("/save")
    public String save(
            @ModelAttribute Movie movie,
            @RequestParam(value = "posterFile", required = false) MultipartFile posterFile,
            @RequestParam(value = "bannerFile", required = false) MultipartFile bannerFile,
            Model model
    ) {

        try {

            if (movie.getTitle() == null || movie.getTitle().trim().isEmpty()) {
                model.addAttribute("error", "Tên phim không được để trống");
                model.addAttribute("activePage","movies");
                return "admin/movies/create";
            }

            if (movie.getDurationMinutes() == null || movie.getDurationMinutes() <= 0) {
                model.addAttribute("error", "Thời lượng phải > 0");
                model.addAttribute("activePage","movies");
                return "admin/movies/create";
            }

            // upload poster
            if (posterFile != null && !posterFile.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + posterFile.getOriginalFilename();

                Path uploadPath = Paths.get(UPLOAD_DIR);
                Files.createDirectories(uploadPath);

                Files.write(uploadPath.resolve(fileName), posterFile.getBytes());

                movie.setPosterUrl("/uploads/movies/" + fileName);
            }

            // upload banner
            if (bannerFile != null && !bannerFile.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_banner_" + bannerFile.getOriginalFilename();

                Path uploadPath = Paths.get(UPLOAD_DIR);
                Files.createDirectories(uploadPath);

                Files.write(uploadPath.resolve(fileName), bannerFile.getBytes());

                movie.setBannerUrl("/uploads/movies/" + fileName);
            }

            movieRepository.save(movie);

            return "redirect:/admin/movies";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            model.addAttribute("activePage","movies");
            return "admin/movies/create";
        }
    }

    /* ===============================
       EDIT PAGE
    =============================== */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model){

        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie không tồn tại"));

        model.addAttribute("movie", movie);
        model.addAttribute("activePage","movies");

        return "admin/movies/edit";
    }

    /* ===============================
       UPDATE
    =============================== */
    @PostMapping("/update")
    public String update(
            @RequestParam Long id,
            @RequestParam String title,
            @RequestParam(required = false) String director,
            @RequestParam(required = false) String actors,
            @RequestParam(required = false) String genres,
            @RequestParam(required = false) Integer durationMinutes,
            @RequestParam(required = false) String releaseDate,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String subtitle,
            @RequestParam(required = false) String ageRating,
            @RequestParam(required = false) String trailerUrl,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String status,
            @RequestParam(value = "posterFile", required = false) MultipartFile posterFile,
            @RequestParam(value = "bannerFile", required = false) MultipartFile bannerFile,
            Model model
    ) {

        System.out.println("UPDATE ID = " + id);

        if (id == null) {
            model.addAttribute("error", "ID không được null");
            model.addAttribute("activePage","movies");
            return "admin/movies/edit";
        }

        try {

            Movie existing = movieRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Movie không tồn tại"));

            existing.setTitle(title);
            existing.setDirector(director);
            existing.setActors(actors);
            existing.setGenres(genres);
            existing.setDurationMinutes(durationMinutes);

            if (releaseDate != null && !releaseDate.isEmpty()) {
                existing.setReleaseDate(java.sql.Date.valueOf(releaseDate).toLocalDate());
            }

            existing.setLanguage(language);
            existing.setSubtitle(subtitle);
            existing.setAgeRating(ageRating);
            existing.setTrailerUrl(trailerUrl);
            existing.setDescription(description);
            existing.setStatus(status);

            // upload poster
            if (posterFile != null && !posterFile.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + posterFile.getOriginalFilename();

                Path uploadPath = Paths.get(UPLOAD_DIR);
                Files.createDirectories(uploadPath);

                Files.write(uploadPath.resolve(fileName), posterFile.getBytes());

                existing.setPosterUrl("/uploads/movies/" + fileName);
            }

            // upload banner
            if (bannerFile != null && !bannerFile.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_banner_" + bannerFile.getOriginalFilename();

                Path uploadPath = Paths.get(UPLOAD_DIR);
                Files.createDirectories(uploadPath);

                Files.write(uploadPath.resolve(fileName), bannerFile.getBytes());

                existing.setBannerUrl("/uploads/movies/" + fileName);
            }

            movieRepository.save(existing);

            return "redirect:/admin/movies";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            model.addAttribute("activePage","movies");
            return "admin/movies/edit";
        }
    }

    /* ===============================
       DELETE
    =============================== */
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id){
        if (showtimeRepository.existsByMovieId(id)) {
            Movie existing = movieRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Movie khÃ´ng tá»“n táº¡i"));
            existing.setStatus("STOPPED");
            movieRepository.save(existing);
            return "redirect:/admin/movies";
        }

        movieRepository.deleteById(id);
        return "redirect:/admin/movies";
    }
}
