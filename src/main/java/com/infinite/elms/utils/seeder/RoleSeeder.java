package com.infinite.elms.utils.seeder;

import com.infinite.elms.models.Role;
import com.infinite.elms.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
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