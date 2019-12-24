package com.example.higerpoint;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.example.higerpoint.*"})
public class HigerpointApplication {
    public static void main(String[] args) {
        SpringApplication.run(HigerpointApplication.class, args);
    }

}
