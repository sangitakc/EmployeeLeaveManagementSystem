package com.infinite.elms;
import com.infinite.elms.dtos.userDTO.UserDTO;
import com.infinite.elms.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infinite.elms.service.AuthService.AuthService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserRegistrationIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    private static final String ADMIN_EMAIL = "admin@leave.com";
    private static final String ADMIN_PASSWORD = "Admin@123";
    private static final String TEST_EMPLOYEE_EMAIL = "testemployee@leave.com";

    @Test
    void adminCanRegisterEmployeeUsingJwt() throws Exception {
        String jwtToken = authService.login(ADMIN_EMAIL, ADMIN_PASSWORD);

        if (userRepository.existsByEmail(TEST_EMPLOYEE_EMAIL)) {
            System.out.println("Test employee already exists. Skipping registration.");
            return;
        }

        UserDTO employee = UserDTO.builder()
                .name("Test Employee")
                .email(TEST_EMPLOYEE_EMAIL)
                .password("Emp@123")
                .build();

        mockMvc.perform(post("/api/employee/register")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Registration successful"))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andDo(result -> System.out.println("Employee registered: " + result.getResponse().getContentAsString()));
    }

    @AfterEach
    void cleanupTestEmployee() {
        deleteUserIfExists(TEST_EMPLOYEE_EMAIL);
    }

    private void deleteUserIfExists(String email) {
        userRepository.findByEmail(email).ifPresent(userRepository::delete);
        System.out.println("Cleaned up employee with email: " + email);
    }
}