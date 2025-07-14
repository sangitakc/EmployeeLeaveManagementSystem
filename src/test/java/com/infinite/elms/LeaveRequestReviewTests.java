package com.infinite.elms;

import com.infinite.elms.constants.LeaveStatus;
import com.infinite.elms.constants.LeaveType;
import com.infinite.elms.dtos.leaveRequestDTO.AdminDecisionDTO;
import com.infinite.elms.dtos.leaveRequestDTO.LeaveRequestDTO;
import com.infinite.elms.dtos.userDTO.UserDTO;
import com.infinite.elms.models.LeavePolicy;
import com.infinite.elms.models.LeaveRequest;
import com.infinite.elms.models.Users;
import com.infinite.elms.repositories.LeavePolicyRepository;
import com.infinite.elms.repositories.LeaveRequestRepository;
import com.infinite.elms.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infinite.elms.service.AuthService.AuthService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class LeaveRequestReviewTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private LeavePolicyRepository leavePolicyRepository;
    @Autowired
    private LeaveRequestRepository leaveRequestRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthService authService;

    private final String employeeEmail = "employee4@example.com";
    private final String employeePassword = "Emp@123";
    private final String adminEmail = "admin@leave.com";
    private final String adminPassword = "Admin@123";

    //    setup a leave policy before each test run
    @BeforeEach
    void setupPolicy() {
        leavePolicyRepository.save(
                LeavePolicy.builder()
                        .leaveType(LeaveType.SICK_LEAVE)
                        .allowedDaysPerYear(10)
                        .policyYear(LocalDate.now().getYear())
                        .build()
        );
    }

    @Test
    void adminApprovesSubmittedLeave() throws Exception {
        String  adminToken= authService.login(adminEmail, adminPassword);

//        Register the employee if not already present
        UserDTO employee = UserDTO.builder()
                .name("Employee1")
                .email(employeeEmail)
                .password(employeePassword)
                .build();

        mockMvc.perform(post("/api/employee/register")
                        .header("Authorization", "Bearer " + adminToken)

                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Registration successful"));

        String employeeToken = authService.login(employeeEmail, employeePassword);


//      submit a new leave request
        LeaveRequestDTO request = new LeaveRequestDTO();
        request.setStartDate(LocalDate.now().plusDays(1));
        request.setEndDate(LocalDate.now().plusDays(2));
        request.setLeaveType(LeaveType.SICK_LEAVE);
        request.setReason("Mild illness");

        MvcResult submitResult = mockMvc.perform(post("/api/leaveRequest/submit")
                        .header("Authorization", "Bearer " + employeeToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Leave request submitted successfully"))
                .andReturn();

        System.out.println("Submit Response: " + submitResult.getResponse().getContentAsString());

        // Admin reviews and approves the request
//        Users employeeEntity = userRepository.findByEmail(employeeEmail).orElseThrow();
//        LeaveRequest latestRequest = leaveRequestRepository.findTopByEmployeeOrderByRequestDateDesc(employeeEntity);
//        assertNotNull(latestRequest, "No leave request found for the employee.");
        // Admin reviews and approves the request
        Users employeeEntity = userRepository.findByEmail(employeeEmail).orElseThrow();
        LeaveRequest latestRequest = leaveRequestRepository
                .findByEmployeeIdAndStatus(employeeEntity.getId(), LeaveStatus.PENDING)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No pending leave request found."));
        assertNotNull(latestRequest, "No leave request found for the employee.");

//        Admin approval
        AdminDecisionDTO decision = new AdminDecisionDTO();
        decision.setStatus(LeaveStatus.APPROVED);
        decision.setComment("Approved. Get well soon!");

        MvcResult approvalResult = mockMvc.perform(patch("/api/leaveRequest/review/" + latestRequest.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(decision)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Leave request has been approved"))
                .andReturn();

        System.out.println("Approval Response: " + approvalResult.getResponse().getContentAsString());
    }

    @AfterEach
    void cleanupTestEmployee() {
        deleteUserIfExists(employeeEmail);
    }
    private void deleteUserIfExists(String email) {
        userRepository.findByEmail(email).ifPresent(userRepository::delete);
        System.out.println("Cleaned up employee with email: " + email);
    }
}


