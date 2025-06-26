package com.infinite.elms.controller;
import com.infinite.elms.dtos.LoginDTO;
import com.infinite.elms.dtos.UpdateEmployeeDTO;
import com.infinite.elms.dtos.UserDTO;
import com.infinite.elms.dtos.UserDetailsResponseDTO;
import com.infinite.elms.service.AuthService;
import com.infinite.elms.service.UserService;
import com.infinite.elms.utils.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/employee/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response<String>> register(@Valid @RequestBody UserDTO userDTO) {
        log.info("Registering user with email: {}", userDTO.getEmail());
        String message = userService.register(userDTO);
        return ResponseEntity.ok(
                Response.<String>builder()
                        .status(HttpStatus.OK.value())
                        .message("Registration successful")
                        .data(message)
                        .build()
        );
    }

    @GetMapping("/employee/getAllEmployees")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response<List<UserDetailsResponseDTO>>> getAllEmployees() {
        log.info("Start: Fetching all employees");
        List<UserDetailsResponseDTO> users = userService.getAllEmployees();
        log.info("Completed: Successfully fetched {} employees", users.size());
        return ResponseEntity.ok(
                Response.<List<UserDetailsResponseDTO>>builder()
                        .status(HttpStatus.OK.value())
                        .message("Employees fetched successfully")
                        .data(users)
                        .build()
        );
    }

    @GetMapping("/employee/getEmployeeById/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response<UserDetailsResponseDTO>> getEmployeeById(@PathVariable Long id) {
        log.info("Start: Fetching employee with ID: {}", id);
        UserDetailsResponseDTO user = userService.getEmployeeById(id);
        log.info("Completed: Successfully fetched employee with ID: {}", id);
        return ResponseEntity.ok(
                Response.<UserDetailsResponseDTO>builder()
                        .status(HttpStatus.OK.value())
                        .message("Employee fetched successfully")
                        .data(user)
                        .build()
        );
    }

    @DeleteMapping("/employee/deleteEmployee/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response<Void>> deleteEmployee(@PathVariable Long id) {
        log.info("Request received to delete employee with ID: {}", id);
        userService.deleteEmployee(id);
        log.info("Successfully deleted employee with ID: {}", id);

        return ResponseEntity.ok(
                Response.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Employee deleted successfully")
                        .build()
        );
    }

    @PutMapping("/employee/updateEmployee/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response<UpdateEmployeeDTO>> updateEmployee(@PathVariable Long id, @RequestBody UpdateEmployeeDTO dto) {
        log.info("Start: Updating employee with ID: {}", id);
        UpdateEmployeeDTO updatedDto = userService.updateEmployee(id, dto);
        log.info("Completed: Successfully updated employee with ID: {}", id);
        return ResponseEntity.ok(
                Response.<UpdateEmployeeDTO>builder()
                        .status(HttpStatus.OK.value())
                        .message("Employee updated successfully")
                        .data(updatedDto)
                        .build()
        );
    }


    @PostMapping("/login")
    public ResponseEntity<Response<String>> login(@Valid @RequestBody LoginDTO loginDTO) {
        log.info("Authenticating user with email: {}", loginDTO.getEmail());
        String token = authService.login(loginDTO.getEmail(), loginDTO.getPassword());
        log.info("Authentication successful for user: {}", loginDTO.getEmail());

        return ResponseEntity.ok(Response.<String>builder()
                .status(HttpStatus.OK.value())
                .message("Login successful")
                .data(token)
                .build());
    }
}

