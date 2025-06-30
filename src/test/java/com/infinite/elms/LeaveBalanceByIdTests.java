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
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class LeaveBalanceByIdTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthService authService;
    @Autowired
    private ObjectMapper objectMapper;

    private String employeeEmail = "employee4@example.com";
    private String employeePassword = "Emp@123";
    private Long employeeId;
    private String employeeToken;

    @BeforeEach
    void setupUserIfNeeded() {
        // Ensure employee is present
        Users user = userRepository.findByEmail(employeeEmail).orElse(null);
        if (user == null) {
            UserDTO employee = UserDTO.builder()
                    .name("Test Employee")
                    .email(employeeEmail)
                    .password(employeePassword)
                    .build();

            authService.registerEmployee(employee); // Assuming this method exists and works
            user = userRepository.findByEmail(employeeEmail).orElseThrow();
        }

        employeeId = user.getId();
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
