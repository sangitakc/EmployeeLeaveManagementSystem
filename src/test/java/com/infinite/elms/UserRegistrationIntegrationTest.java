package com.infinite.elms;


import com.infinite.elms.dtos.LoginDTO;

import com.infinite.elms.dtos.UserDTO;

import com.infinite.elms.repositories.UserRepository;

import com.fasterxml.jackson.databind.JsonNode;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.infinite.elms.models.Users;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.fail;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest

@AutoConfigureMockMvc

class UserRegistrationIntegrationTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @Autowired private UserRepository userRepository;

    private static final String ADMIN_EMAIL = "admin@leave.com";

    private static final String ADMIN_PASSWORD = "Admin@123";

    private static final String TEST_EMAIL = "employee@gmail.com";

    @Test

    void adminCanRegisterEmployeeUsingJwt() throws Exception {

        //Admin login

        LoginDTO loginDTO = new LoginDTO();

        loginDTO.setEmail(ADMIN_EMAIL);

        loginDTO.setPassword(ADMIN_PASSWORD);

        String loginResponse = mockMvc.perform(post("/api/login")

                        .contentType(MediaType.APPLICATION_JSON)

                        .content(objectMapper.writeValueAsString(loginDTO)))

                .andExpect(status().isOk())

                .andReturn()

                .getResponse()

                .getContentAsString();

        String jwt = extractJwtToken(loginResponse);

        if (jwt == null || jwt.isBlank()) {

            fail("JWT token not found in login response.");

        }

        // Skip registration if the test user already exists

        if (userRepository.existsByEmail(TEST_EMAIL)) {

            System.out.println("Test user already exists.");

            return;

        }

        // Register a new employee

        UserDTO newUser = UserDTO.builder()

                .name("Test Employee")

                .email(TEST_EMAIL)

                .password("Emp@123")

                .build();

        mockMvc.perform(post("/api/employee/register")

                        .header("Authorization", "Bearer " + jwt)

                        .contentType(MediaType.APPLICATION_JSON)

                        .content(objectMapper.writeValueAsString(newUser)))

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.message").value("Registration successful"))

                .andExpect(jsonPath("$.data").isNotEmpty())

                .andDo(result -> System.out.println("Employee registered: " + result.getResponse().getContentAsString()));

    }

    private String extractJwtToken(String json) throws Exception {

        JsonNode root = objectMapper.readTree(json);

        if (!root.has("data")) {

            throw new IllegalStateException("Login response missing 'data' field with token");

        }

        return root.get("data").asText();

    }

}


