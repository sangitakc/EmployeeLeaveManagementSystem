package com.infinite.elms;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infinite.elms.dtos.userDTO.UserDTO;
import com.infinite.elms.models.Users;
import com.infinite.elms.repositories.UserRepository;

import com.infinite.elms.service.AuthService.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = ElmsApplication.class)
@AutoConfigureMockMvc
public class DeleteEmployeeByIdTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private AuthService authService;

    private static final String ADMIN_EMAIL = "admin@leave.com";
    private static final String ADMIN_PASSWORD = "Admin@123";
    private static final String EMPLOYEE_EMAIL = "delete.employee@leave.com";
    private static final String EMPLOYEE_PASSWORD = "Delete123";

    @BeforeEach
    void setUp() {
        userRepository.findByEmail(EMPLOYEE_EMAIL)
                .ifPresent(user -> userRepository.deleteById(user.getId()));
    }

    @Test
    void adminCanDeleteEmployeeById() throws Exception {
        String jwtToken= authService.login(ADMIN_EMAIL,ADMIN_PASSWORD);

        UserDTO employeeToDelete = UserDTO.builder()
                .name("Delete Test")
                .email(EMPLOYEE_EMAIL)
                .password(EMPLOYEE_PASSWORD)
                .build();

        mockMvc.perform(post("/api/employee/register")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeToDelete)))
                .andExpect(status().isOk());

        Users savedEmployee = userRepository.findByEmail(EMPLOYEE_EMAIL)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        Long employeeId = savedEmployee.getId();
        assertNotNull(employeeId);

        mockMvc.perform(delete("/api/employee/deleteEmployee/" + employeeId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Employee deleted successfully"));
    }
}

