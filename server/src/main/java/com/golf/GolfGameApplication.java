package com.golf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Main Spring Boot application for Golf Card Game Backend
 */
@SpringBootApplication
public class GolfGameApplication {

    private static final Logger logger = LoggerFactory.getLogger(GolfGameApplication.class);

    public static void main(String[] args) {
        logger.info("Starting Golf Card Game Backend...");
        SpringApplication.run(GolfGameApplication.class, args);
        logger.info("Golf Card Game Backend started successfully!");
    }

    /**
     * Configure CORS to allow frontend connections
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                logger.debug("Configuring CORS mappings");
                registry.addMapping("/api/**")
                        .allowedOrigins(
                                "http://localhost:3000",
                                "http://localhost:5173",
                                "http://127.0.0.1:5500"
                        )
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
