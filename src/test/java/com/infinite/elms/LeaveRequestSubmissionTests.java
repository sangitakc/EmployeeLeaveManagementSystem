package com.infinite.elms;


import com.infinite.elms.constants.LeaveType;
import com.infinite.elms.dtos.LeaveRequestDTO;
import com.infinite.elms.dtos.LoginDTO;
import com.infinite.elms.dtos.UserDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LeaveRequestSubmissionTests {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    private final String adminEmail = "admin@leave.com";
    private final String adminPassword = "Admin@123";
    private final String employeeEmail = "employee7@example.com";
    private final String employeePassword = "Emp@123";

    @Test
    void EmployeeSubmitsLeaveRequest() throws Exception {
        // Admin login
        String adminToken = authenticate(adminEmail, adminPassword);
        assertNotNull(adminToken);

        // Admin registers new employee
        UserDTO employee = UserDTO.builder()
                .name("Employee1")
                .email(employeeEmail)
                .password(employeePassword)
                .build();

        mockMvc.perform(post("/api/employee/register")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Registration successful"));

        // Employee login
        String employeeToken = authenticate(employeeEmail, employeePassword);
        assertNotNull(employeeToken);

        // Employee leave request submit
        LeaveRequestDTO leaveRequest = new LeaveRequestDTO();
        leaveRequest.setStartDate(LocalDate.now().plusDays(1));
        leaveRequest.setEndDate(LocalDate.now().plusDays(3));
        leaveRequest.setLeaveType(LeaveType.CASUAL_LEAVE);
        leaveRequest.setReason("Wedding Ceremony");

        mockMvc.perform(post("/api/leaveRequest/submit")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + employeeToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(leaveRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Leave request submitted successfully"));
    }

    private String authenticate(String email, String password) throws Exception {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail(email);
        loginDTO.setPassword(password);

        String response = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(response);
        return jsonNode.get("data").asText();
    }
}