package com.switchmanga.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Switch Manga API is running! 🚀";
    }

    @GetMapping("/status")
    public String status() {
        return "Status: OK";
    }
}