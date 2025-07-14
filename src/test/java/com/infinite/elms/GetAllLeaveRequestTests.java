package com.infinite.elms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infinite.elms.repositories.UserRepository;
import com.infinite.elms.service.AuthService.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GetAllLeaveRequestTests {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired
    private AuthService authService;
    @Autowired
    private UserRepository userRepository;

    private final String adminEmail = "admin@leave.com";
    private final String adminPassword = "Admin@123";

    @Test
    void adminCanFetchAllLeaveRequests() throws Exception {
        // Step 1: Login as admin
        String jwtToken = authService.login(adminEmail,adminPassword);

        // Step 2: Fetch leave requests
        MvcResult result = mockMvc.perform(get("/api/leaveRequest/getAllLeaveRequest")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON
                        ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        System.out.println("Get All Leave Requests Response: " + content);
    }

}
