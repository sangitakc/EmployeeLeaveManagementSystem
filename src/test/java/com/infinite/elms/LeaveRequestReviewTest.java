package com.infinite.elms;

import com.infinite.elms.constants.LeaveStatus;
import com.infinite.elms.constants.LeaveType;
import com.infinite.elms.dtos.AdminDecisionDTO;
import com.infinite.elms.dtos.LeaveRequestDTO;
import com.infinite.elms.dtos.LoginDTO;
import com.infinite.elms.dtos.UserDTO;
import com.infinite.elms.models.LeavePolicy;
import com.infinite.elms.models.LeaveRequest;
import com.infinite.elms.models.Users;
import com.infinite.elms.repositories.LeavePolicyRepository;
import com.infinite.elms.repositories.LeaveRequestRepository;
import com.infinite.elms.repositories.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
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
public class LeaveRequestReviewTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private LeavePolicyRepository leavePolicyRepository;
    @Autowired private LeaveRequestRepository leaveRequestRepository;
    @Autowired private UserRepository userRepository;

    private final String employeeEmail = "employee3@example.com";
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
//        Admin logs in and gets JWT
        String adminToken = loginAndGetToken(adminEmail, adminPassword);
        assertNotNull(adminToken);

//        Register the employee if not already present
        if (!userRepository.existsByEmail(employeeEmail)) {
            UserDTO employee = UserDTO.builder()
                    .name("Reviewer")
                    .email(employeeEmail)
                    .password(employeePassword)
                    .build();

            mockMvc.perform(post("/api/employee/register")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(employee)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Registration successful"));
        }

//        Employee logs in and gets token
        String employeeToken = loginAndGetToken(employeeEmail, employeePassword);
        assertNotNull(employeeToken);

//        prepare and submit a new leave request
        LeaveRequestDTO request = new LeaveRequestDTO();
        request.setStartDate(LocalDate.now().plusDays(1));
        request.setEndDate(LocalDate.now().plusDays(2));
        request.setLeaveType(LeaveType.SICK_LEAVE);
        request.setReason("Mild illness");

        MvcResult submitResult = mockMvc.perform(post("/api/leaveRequest/submit")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + employeeToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Leave request submitted successfully"))
                .andReturn();

        System.out.println("Submit Response: " + submitResult.getResponse().getContentAsString());

        //Fetch the submitted leave request
        Users employee = userRepository.findByEmail(employeeEmail).orElseThrow();
        LeaveRequest latestRequest = leaveRequestRepository.findTopByEmployeeOrderByRequestDateDesc(employee);
        Long requestId = latestRequest.getId();
        assertNotNull(requestId);

//        Admin approval
        AdminDecisionDTO decision = new AdminDecisionDTO();
        decision.setStatus(LeaveStatus.APPROVED);
        decision.setComment("Approved. Get well soon!");

        MvcResult approvalResult = mockMvc.perform(patch("/api/leaveRequest/review/" + requestId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(decision)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Leave request has been approved"))
                .andReturn();

        System.out.println("Approval Response: " + approvalResult.getResponse().getContentAsString());
    }

    //    Get token from login response
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