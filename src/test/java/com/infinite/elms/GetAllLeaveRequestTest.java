package com.infinite.elms;


import com.infinite.elms.dtos.LoginDTO;

import com.fasterxml.jackson.databind.JsonNode;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.HttpHeaders;

import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest

@AutoConfigureMockMvc

public class GetAllLeaveRequestTest{

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    private final String adminEmail = "admin@leave.com";

    private final String adminPassword = "Admin@123";

    @Test

    void adminCanFetchAllLeaveRequests() throws Exception {

        // Step 1: Login as admin

        String adminToken = loginAndGetToken(adminEmail, adminPassword);

        assertNotNull(adminToken);

        // Step 2: Fetch leave requests

        MvcResult result = mockMvc.perform(get("/api/leaveRequest/getAllLeaveRequest")

                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)

                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.message").exists())

                .andReturn();

        String content = result.getResponse().getContentAsString();

        System.out.println("Get All Leave Requests Response: " + content);

    }

    private String loginAndGetToken(String email, String password) throws Exception {

        LoginDTO login = new LoginDTO();

        login.setEmail(email);

        login.setPassword(password);

        String response = mockMvc.perform(post("/api/login")

                        .contentType(MediaType.APPLICATION_JSON)

                        .content(objectMapper.writeValueAsString(login)))

                .andExpect(status().isOk())

                .andReturn().getResponse().getContentAsString();

        JsonNode root = objectMapper.readTree(response);

        return root.get("data").asText();

    }

}

