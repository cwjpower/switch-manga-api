package com.switchmanga.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, Object> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("service", "Switch Manga API");
        response.put("version", "1.0.0");
        response.put("message", "Welcome to Switch Manga API! 🚀");
        response.put("swagger", "http://localhost:8080/swagger-ui.html");
        return response;
    }

    @GetMapping("/test")
    public Map<String, Object> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Server is running!");
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
}