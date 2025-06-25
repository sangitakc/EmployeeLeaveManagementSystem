package com.infinite.elms.utils;
import com.infinite.elms.models.Role;
import com.infinite.elms.models.Users;
import com.infinite.elms.repositories.RoleRepository;
import com.infinite.elms.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository usersRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!usersRepository.existsByEmail("admin@leave.com")) {
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseThrow(() -> new RuntimeException("ADMIN role not found"));

            Users admin = Users.builder()
                    .name("System Admin")
                    .email("admin@leave.com")
                    .password(passwordEncoder.encode("Admin@123"))
                    .role(adminRole)
                    .build();

            usersRepository.save(admin);
        }
    }
}

