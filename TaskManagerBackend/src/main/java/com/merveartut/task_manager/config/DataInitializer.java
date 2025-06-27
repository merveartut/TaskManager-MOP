package com.merveartut.task_manager.config;

import com.merveartut.task_manager.enums.Role;
import com.merveartut.task_manager.model.User;
import com.merveartut.task_manager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;

    @Bean
    CommandLineRunner init() {
        return args -> {
            if (!userRepository.existsByEmail("admin@example.com")) {
                User admin = User.builder()
                        .name("Admin")
                        .email("admin@example.com")
                        .password(new BCryptPasswordEncoder().encode("admin123"))
                        .role(Role.ADMIN) // Make sure Role.ADMIN exists
                        .build();

                userRepository.save(admin);
                System.out.println("✅ Default admin user created.");
            } else {
                System.out.println("ℹ️ Admin user already exists.");
            }
        };
    }
}
