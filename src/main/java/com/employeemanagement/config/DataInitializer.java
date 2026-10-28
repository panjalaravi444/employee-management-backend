package com.employeemanagement.config;

import com.employeemanagement.entity.User;
import com.employeemanagement.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .roles(java.util.Set.of("ADMIN", "USER"))
                    .enabled(true)
                    .build();
            userRepository.save(admin);
            System.out.println("Default admin user created: username=admin, password=admin123");
        }

        if (!userRepository.existsByUsername("user")) {
            User user = User.builder()
                    .username("user")
                    .password(passwordEncoder.encode("user123"))
                    .roles(java.util.Set.of("USER"))
                    .enabled(true)
                    .build();
            userRepository.save(user);
            System.out.println("Default user created: username=user, password=user123");
        }
    }
}
