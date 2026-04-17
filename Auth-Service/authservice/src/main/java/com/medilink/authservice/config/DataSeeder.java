package com.medilink.authservice.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    @Override
    public void run(String... args) {
        // Intentionally left blank.
        // Auth records should come from real registration flows, not hardcoded seed users.
    }
}
