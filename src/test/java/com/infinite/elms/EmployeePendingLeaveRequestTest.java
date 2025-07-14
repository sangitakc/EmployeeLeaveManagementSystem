package com.infinite.elms;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infinite.elms.models.Users;
import com.infinite.elms.repositories.RoleRepository;
import com.infinite.elms.repositories.UserRepository;
import com.infinite.elms.service.AuthService.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class EmployeePendingLeaveRequestTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AuthService authService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;


    private static final String EMPLOYEE_EMAIL = "dipikat@infinite.com";
    private static final String EMPLOYEE_PASSWORD = "dipika123";
    private static final Long EMPLOYEE_USER_ID = 2L;

    @BeforeEach
    void setup() {
        Users user = new Users();
        user.setEmail("dipikat@infinite.com");
        user.setPassword(passwordEncoder.encode("dipika123"));
        user.setName("dipika");
        user.setRole(roleRepository.findByName("EMPLOYEE").orElseThrow());
        userRepository.save(user);
    }


    @Test
    void employeeCanFetchOwnPendingLeaveRequestsByUserId() throws Exception {
        String token = authService.login(EMPLOYEE_EMAIL, EMPLOYEE_PASSWORD);
        assertNotNull(token);
        String response = mockMvc.perform(get("/api/leaveRequest/getPendingLeaveRequestsByUserId/" + EMPLOYEE_USER_ID)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        System.out.println("Pending Leave Request Response: " + response);


        mockMvc.perform(get("/api/leaveRequest/getPendingLeaveRequestsByUserId/" + EMPLOYEE_USER_ID)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.message").value("Pending leave requests fetched successfully for user ID: " + EMPLOYEE_USER_ID));
    }
}
