package com.example.PopcornCinema.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.ui.Model;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e, Model model) {
        System.out.println("FILE SIZE EXCEEDED EXCEPTION CAUGHT");
        System.out.println("Message: " + e.getMessage());
        model.addAttribute("error", "File quá lớn! Vui lòng chọn file nhỏ hơn 500MB");
        model.addAttribute("activePage","movies");
        return "admin/movies/create";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {
        System.out.println("GENERAL EXCEPTION: " + e.getClass().getSimpleName());
        System.out.println("Message: " + e.getMessage());
        e.printStackTrace();
        model.addAttribute("error", "Lỗi: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        model.addAttribute("activePage","movies");
        return "admin/movies/create";
    }
}
