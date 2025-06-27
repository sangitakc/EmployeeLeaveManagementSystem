package com.infinite.elms;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infinite.elms.dtos.LoginDTO;
import com.infinite.elms.dtos.UserDTO;
import com.infinite.elms.models.Users;
import com.infinite.elms.repositories.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = ElmsApplication.class)
@AutoConfigureMockMvc
public class GetEmployeeByIdTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;

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
        // Step 1: Obtain JWT token
        String jwt = obtainJwtToken();
        assertNotNull(jwt);

        // Step 2: Register a new employee
        UserDTO newEmployee = UserDTO.builder()
                .name("Test Employee")
                .email(EMPLOYEE_EMAIL)
                .password(EMPLOYEE_PASSWORD)
                .build();

        mockMvc.perform(post("/api/employee/register")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newEmployee)))
                .andExpect(status().isOk());

        // Step 3: Retrieve saved employee from DB
        Users savedEmployee = userRepository.findByEmail(EMPLOYEE_EMAIL)
                .orElseThrow(() -> new RuntimeException("Test employee not found"));

        Long employeeId = savedEmployee.getId();
        assertNotNull(employeeId);

        // Step 4: Get employee by ID
        mockMvc.perform(get("/api/employee/getEmployeeById/" + employeeId)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.email").value(EMPLOYEE_EMAIL))
                .andExpect(jsonPath("$.message").value("Employee fetched successfully"));
    }

    private String obtainJwtToken() throws Exception {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail(ADMIN_EMAIL);
        loginDTO.setPassword(ADMIN_PASSWORD);

        MvcResult result = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(response);
        return root.path("data").asText();
    }
}


