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
public class DeleteEmployeeByIdTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;

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
        // Step 1: Authenticate admin
        String jwt = obtainJwtToken();
        assertNotNull(jwt);

        // Step 2: Register employee to be deleted
        UserDTO employeeToDelete = UserDTO.builder()
                .name("Delete Test")
                .email(EMPLOYEE_EMAIL)
                .password(EMPLOYEE_PASSWORD)
                .build();

        mockMvc.perform(post("/api/employee/register")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeToDelete)))
                .andExpect(status().isOk());

        // Step 3: Fetch employee ID from DB
        Users savedEmployee = userRepository.findByEmail(EMPLOYEE_EMAIL)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        Long employeeId = savedEmployee.getId();
        assertNotNull(employeeId);

        // Step 4: Perform DELETE
        mockMvc.perform(delete("/api/employee/deleteEmployee/" + employeeId)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Employee deleted successfully"));
    }

    private String obtainJwtToken() throws Exception {
        LoginDTO login = new LoginDTO();
        login.setEmail(ADMIN_EMAIL);
        login.setPassword(ADMIN_PASSWORD);

        MvcResult result = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(json);
        return root.path("data").asText();
    }
}
