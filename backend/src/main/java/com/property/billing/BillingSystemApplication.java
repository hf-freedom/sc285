package com.property.billing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@EnableScheduling
@CrossOrigin(origins = "http://localhost:3006")
public class BillingSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(BillingSystemApplication.class, args);
    }
}
