package com.infinite.elms.utils;

import com.infinite.elms.models.Role;
import com.infinite.elms.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        if (!roleRepository.existsByName("EMPLOYEE")) {
            roleRepository.save(Role.builder().name("EMPLOYEE").build());
        }
        if (!roleRepository.existsByName("ADMIN")) {
            roleRepository.save(Role.builder().name("ADMIN").build());
        }
    }
}