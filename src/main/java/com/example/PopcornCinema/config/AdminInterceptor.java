package com.example.PopcornCinema.config;

import com.example.PopcornCinema.entity.User;
import jakarta.servlet.http.*;

import org.springframework.web.servlet.HandlerInterceptor;

public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        // chưa login hoặc không phải admin
        if(user == null || !"ADMIN".equals(user.getRole())){
            response.sendRedirect("/login");
            return false;
        }

        return true;
    }
}