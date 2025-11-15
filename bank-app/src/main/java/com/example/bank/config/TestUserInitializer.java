package com.example.bank.config;

import com.example.bank.domain.user.model.Role;
import com.example.bank.domain.user.model.User;
import com.example.bank.domain.user.repository.RoleRepository;
import com.example.bank.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class TestUserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.findByUsername("admin").isEmpty()) {
            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_USER").build()));
            User user = new User();
            user.setUsername("admin");
            user.setPassword(passwordEncoder.encode("admin123"));
            user.setRoles(Set.of(userRole));

            userRepository.save(user);
        }
    }
}
