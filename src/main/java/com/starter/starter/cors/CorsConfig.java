package com.starter.starter.cors;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**") // Autoriser toutes les routes API
                        .allowedOrigins("http://localhost:5173") // Autoriser Vue.js
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Autoriser ces méthodes
                        .allowCredentials(true) // Autoriser les cookies (important pour les tokens)
                        .allowedHeaders("*"); // Accepter tous les headers
            }
        };
    }
}
