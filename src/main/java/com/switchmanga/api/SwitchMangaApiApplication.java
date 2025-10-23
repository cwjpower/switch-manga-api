package com.switchmanga.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.switchmanga.api")
public class SwitchMangaApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SwitchMangaApiApplication.class, args);
    }
}