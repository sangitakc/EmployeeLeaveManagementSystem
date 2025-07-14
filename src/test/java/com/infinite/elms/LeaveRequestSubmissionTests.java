package com.infinite.elms;
import com.infinite.elms.constants.LeaveType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infinite.elms.dtos.leaveRequestDTO.LeaveRequestDTO;
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
import java.time.LocalDate;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LeaveRequestSubmissionTests {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Autowired
    private AuthService authService;
    @Autowired
    private UserRepository userRepository;

    private final String adminEmail = "admin@leave.com";
    private final String adminPassword = "Admin@123";
    private final String employeeEmail = "employee11@example.com";
    private final String employeePassword = "Emp@123";

    @Test
    void EmployeeSubmitsLeaveRequest() throws Exception {
        // Admin login
        String jwtToken = authService.login(adminEmail, adminPassword);

        // Admin registers new employee
        UserDTO employee = UserDTO.builder()
                .name("Employee1")
                .email(employeeEmail)
                .password(employeePassword)
                .build();

        mockMvc.perform(post("/api/employee/register")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Registration successful"));

        // Step 3: Login as employee to submit leave request
        String employeeToken = authService.login(employeeEmail, employeePassword);

        // Employee leave request submit
        LeaveRequestDTO leaveRequest = new LeaveRequestDTO();
        leaveRequest.setStartDate(LocalDate.now().plusDays(1));
        leaveRequest.setEndDate(LocalDate.now().plusDays(3));
        leaveRequest.setLeaveType(LeaveType.CASUAL_LEAVE);
        leaveRequest.setReason("Wedding Ceremony");

        mockMvc.perform(post("/api/leaveRequest/submit")
                        .header("Authorization", "Bearer " + employeeToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(leaveRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Leave request submitted successfully"));
    }

    @BeforeEach
    void cleanupTestEmployee() {
        deleteUserIfExists(employeeEmail);
    }
    private void deleteUserIfExists(String email) {
        userRepository.findByEmail(email).ifPresent(userRepository::delete);
        System.out.println("Cleaned up employee with email: " + email);
    }

}