package com.example.PopcornCinema.config;

import com.example.PopcornCinema.config.AdminInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new AdminInterceptor())
                .addPathPatterns("/admin/**"); // chỉ chặn admin
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //file upload từ folder uploads/
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
        registry.addResourceHandler("/favicon.ico")
                .addResourceLocations("classpath:/static/");
    }

}