package com.example.PopcornCinema.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new AdminInterceptor())
                .addPathPatterns("/admin/**"); // chá»‰ cháº·n admin
        registry.addInterceptor(new UserInterceptor())
                .addPathPatterns(
                        "/seats",
                        "/payment",
                        "/checkout",
                        "/checkout-qr",
                        "/sepay/**",
                        "/promotions/save",
                        "/api/showtimes/**",
                        "/api/payment-transactions/**",
                        "/api/promotions/active",
                        "/api/tickets/**"
                )
                .excludePathPatterns(
                        "/sepay/return",
                        "/payment-failed",
                        "/ticket-success",
                        "/api/tickets/success-info"
                );
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //file upload tá»« folder uploads/
        String uploadDir = java.nio.file.Paths.get("uploads").toAbsolutePath().toUri().toString();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadDir);
        registry.addResourceHandler("/favicon.ico")
                .addResourceLocations("classpath:/static/");
    }

}
