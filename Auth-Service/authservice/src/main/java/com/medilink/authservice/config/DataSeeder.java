package com.medilink.authservice.config;

import com.medilink.authservice.model.User;
import com.medilink.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedUser("Jane Smith", "ayesh@gmail.com", "Ayesh@1111", "PATIENT", true, "+1987654321");
        seedUser("Anjana Perera", "anjana@gmail.com", "Anjana@2222", "DOCTOR", true, "0771234567");
        seedUser("Medilink Admin", "admin@medilink.com", "Password@5555", "ADMIN", true, "0110000000");
    }

    private void seedUser(String name, String email, String rawPassword, String role, boolean approved, String phone) {
        if (userRepository.existsByEmail(email)) {
            return;
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        user.setIsApproved(approved);
        user.setIsActive(true);
        user.setPhoneNumber(phone);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
    }
}
