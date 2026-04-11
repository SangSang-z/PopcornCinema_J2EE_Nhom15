package com.example.PopcornCinema.controller;

import com.example.PopcornCinema.entity.User;
import com.example.PopcornCinema.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService){
        this.userService = userService;
    }

    // ===============================
    // LOGIN PAGE
    // ===============================
    @GetMapping("/login")
    public String loginPage(){
        return "auth/login";
    }

    // ===============================
    // LOGIN HANDLE
    // ===============================
    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        Model model){

        // VALIDATE EMPTY
        if(email == null || email.trim().isEmpty()){
            model.addAttribute("error", "Vui lòng nhập email");
            return "auth/login";
        }

        if(password == null || password.trim().isEmpty()){
            model.addAttribute("error", "Vui lòng nhập mật khẩu");
            return "auth/login";
        }

        User user = userService.findByEmail(email);

        if(user == null || !user.getPasswordHash().equals(password)){
            model.addAttribute("error", "Vui lòng nhập lại email hoặc mật khẩu");
            return "auth/login";
        }

        // CHECK STATUS
        if(!"ACTIVE".equals(user.getStatus())){
            model.addAttribute("error", "Tài khoản đã bị khóa");
            return "auth/login";
        }

        // SUCCESS
        session.setAttribute("user", user);

        if("ADMIN".equals(user.getRole())){
            return "redirect:/admin/dashboard";
        }

        return "redirect:/home";
    }

    // ===============================
    // REGISTER PAGE
    // ===============================
    @GetMapping("/register")
    public String registerPage(){
        return "auth/register";
    }

    // ===============================
    // REGISTER HANDLE
    // ===============================
    @PostMapping("/register")
    public String register(User user, Model model){

        // VALIDATE
        if(user.getEmail() == null || user.getEmail().isEmpty()){
            model.addAttribute("error", "Email không được để trống");
            return "auth/register";
        }

        // passwordHash
        if(user.getPasswordHash() == null || user.getPasswordHash().isEmpty()){
            model.addAttribute("error", "Mật khẩu không được để trống");
            return "auth/register";
        }

        if(user.getFullName() == null || user.getFullName().isEmpty()){
            model.addAttribute("error", "Tên không được để trống");
            return "auth/register";
        }

        // EMAIL TRÙNG
        if(userService.findByEmail(user.getEmail()) != null){
            model.addAttribute("error", "Email đã tồn tại");
            return "auth/register";
        }

        // SET DEFAULT
        user.setRole("CUSTOMER");
        user.setStatus("ACTIVE");

        userService.register(user);

        return "redirect:/login";
    }

    // ===============================
    // LOGOUT
    // ===============================
    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:/login";
    }

    // ===============================
    // FORGOT PASSWORD
    // ===============================
    @GetMapping("/forgot-password")
    public String forgotPassword(){
        return "auth/forgot-password";
    }

    // ===============================
    // HOME
    // ===============================
    @GetMapping("/home")
    public String home(){
        return "redirect:/";
    }
}
