package com.baranproject.backendmastery.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.baranproject.backendmastery.entity.Role;
import com.baranproject.backendmastery.entity.User;
import com.baranproject.backendmastery.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByEmail("admin@masterymarket.com").isEmpty()) {
            User admin = User.builder()
                    .fullName("yönetici")
                    .email("admin@masterymarket.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ROLE_ADMIN)
                    .build();
            userRepository.save(admin);

        }
        if (userRepository.findByEmail("user@masterymarket.com").isEmpty()) {
            User user = User.builder()
                    .fullName("Baran Çil")
                    .email("user@masterymarket.com")
                    .password(passwordEncoder.encode("user123"))
                    .role(Role.ROLE_USER)
                    .build();
            userRepository.save(user);
        }

    }

}
