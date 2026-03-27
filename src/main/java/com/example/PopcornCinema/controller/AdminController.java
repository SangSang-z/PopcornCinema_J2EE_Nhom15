package com.example.PopcornCinema.controller;

import com.example.PopcornCinema.entity.User;
import com.example.PopcornCinema.repository.MovieRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private MovieRepository movieRepository;

    /* ===============================
       CHECK ADMIN
    =============================== */
    private boolean isAdmin(HttpSession session){
        User user = (User) session.getAttribute("user");
        return user != null && "ADMIN".equals(user.getRole());
    }

    /* ===============================
       /admin
    =============================== */
    @GetMapping
    public String adminHome(HttpSession session) {

        if(!isAdmin(session)){
            return "redirect:/login";
        }

        return "redirect:/admin/dashboard";
    }

    /* ===============================
       /admin/dashboard
    =============================== */
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model){

        if(!isAdmin(session)){
            return "redirect:/login";
        }

        // active menu
        model.addAttribute("activePage","dashboard");

        // ===============================
        // MOCK DATA 
        // ===============================
        model.addAttribute("totalRevenue", 1200000000);
        model.addAttribute("totalTickets", 12540);
        model.addAttribute("newUsers", 320);
        model.addAttribute("totalOrders", 1240);

        // ===============================
        // MOVIES
        // ===============================
        model.addAttribute("topMovies", movieRepository.findAll()); 
        model.addAttribute("nowShowingMovies", movieRepository.findAll());

        return "admin/dashboard";
    }
}