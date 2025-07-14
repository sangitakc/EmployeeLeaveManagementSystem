package com.infinite.elms;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infinite.elms.dtos.userDTO.UserDTO;
import com.infinite.elms.repositories.UserRepository;
import com.infinite.elms.service.AuthService.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class LeaveBalanceByIdTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AuthService authService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;

    private final String employeeEmail = "employee4@example.com";
    private final String employeePassword = "Emp@123";
    private Long employeeId;
    private String employeeToken;

    @BeforeEach
    void setupUserAndLogin() throws Exception {
        String adminToken = authService.login("admin@leave.com", "Admin@123");

        if (userRepository.findByEmail(employeeEmail).isEmpty()) {
            UserDTO employee = UserDTO.builder()
                    .name("Test Employee")
                    .email(employeeEmail)
                    .password(employeePassword)
                    .build();

            mockMvc.perform(post("/api/employee/register")
                            .header("Authorization", "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(employee)))
                    .andExpect(status().isOk());
        }

        employeeId = userRepository.findByEmail(employeeEmail).orElseThrow().getId();
        employeeToken = authService.login(employeeEmail, employeePassword);
    }

    @Test
    void testGetLeaveBalanceByUserId() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/leaveBalance/getLeaveBalance/" + employeeId)
                        .header("Authorization", "Bearer " + employeeToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Leave balance fetched successfully"))
                .andExpect(jsonPath("$.data").exists())
                .andReturn();

        System.out.println("Leave Balance Response: " + result.getResponse().getContentAsString());
    }
}