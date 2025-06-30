package com.infinite.elms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infinite.elms.dtos.userDTO.LoginDTO;
import com.infinite.elms.repositories.UserRepository;
import com.infinite.elms.service.AuthService.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class GetAllEmployeeTests {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private AuthService authService;

    private static final String ADMIN_EMAIL = "admin@leave.com";
    private static final String ADMIN_PASSWORD = "Admin@123";

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllEmployeeTests() throws Exception {
        // Authenticate and get JWT token
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail(ADMIN_EMAIL);
        loginDTO.setPassword(ADMIN_PASSWORD);
        String jwtToken = authService.login(ADMIN_EMAIL, ADMIN_PASSWORD);

        // Perform GET request to fetch all employees
        mockMvc.perform(get("/api/employee/getAllEmployees")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Employees fetched successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andDo(result -> {
                    String json = result.getResponse().getContentAsString();
                    System.out.println("GetAllEmployees response: " + json);
                });
    }
}
