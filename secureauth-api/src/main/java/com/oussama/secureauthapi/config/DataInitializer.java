package com.oussama.secureauthapi.config;

import com.oussama.secureauthapi.entity.Role;
import com.oussama.secureauthapi.entity.RoleName;
import com.oussama.secureauthapi.entity.User;
import com.oussama.secureauthapi.repository.RoleRepository;
import com.oussama.secureauthapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        Role userRole = createRoleIfNotExists(RoleName.ROLE_USER);
        Role adminRole = createRoleIfNotExists(RoleName.ROLE_ADMIN);

        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = User.builder()
                    .firstName("System")
                    .lastName("Admin")
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .enabled(true)
                    .roles(Set.of(userRole, adminRole))
                    .build();

            userRepository.save(admin);
        }
    }

    private Role createRoleIfNotExists(RoleName roleName) {
        return roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(
                        Role.builder()
                                .name(roleName)
                                .build()
                ));
    }
}