package com.vcf.virtuallibrary.controller;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/books/**")
                .allowedOrigins("http://localhost:3000") // Changed back to port 3000
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*");
}

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/**")

                .addResourceLocations("classpath:/static/");
    }
}