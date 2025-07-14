package com.infinite.elms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infinite.elms.dtos.userDTO.UserDTO;
import com.infinite.elms.models.Users;
import com.infinite.elms.repositories.UserRepository;

import com.infinite.elms.service.AuthService.AuthService;
import com.infinite.elms.service.UserService.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = ElmsApplication.class)
@AutoConfigureMockMvc
public class GetEmployeeByIdTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private AuthService authService;
    @Autowired private UserService userService;

    private static final String ADMIN_EMAIL = "admin@leave.com";
    private static final String ADMIN_PASSWORD = "Admin@123";
    private static final String EMPLOYEE_EMAIL = "test.employee@leave.com";
    private static final String EMPLOYEE_PASSWORD = "Emp@123";

    @BeforeEach
    void setUp() {
        userRepository.findByEmail(EMPLOYEE_EMAIL)
                .ifPresent(user -> userRepository.deleteById(user.getId()));
    }

    @Test
    void adminCanGetEmployeeById() throws Exception {
        String jwtToken = authService.login(ADMIN_EMAIL,ADMIN_PASSWORD);


        UserDTO newEmployee = UserDTO.builder()
                .name("Test Employee")
                .email(EMPLOYEE_EMAIL)
                .password(EMPLOYEE_PASSWORD)
                .build();
        userService.register(newEmployee);

        Users savedEmployee = userRepository.findByEmail(EMPLOYEE_EMAIL)
                .orElseThrow(() -> new RuntimeException("Test employee not found"));

        Long employeeId = savedEmployee.getId();
        assertNotNull(employeeId);

        String response = mockMvc.perform(get("/api/employee/getEmployeeById/" + employeeId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        System.out.println("Employee by ID API Response: " + response);

        mockMvc.perform(get("/api/employee/getEmployeeById/" + employeeId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.email").value(EMPLOYEE_EMAIL))
                .andExpect(jsonPath("$.message").value("Employee fetched successfully"));
    }

}



