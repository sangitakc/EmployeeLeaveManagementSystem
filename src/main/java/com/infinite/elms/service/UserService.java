package com.infinite.elms.service;

import com.infinite.elms.dtos.UpdateEmployeeDTO;
import com.infinite.elms.dtos.UserDTO;
import com.infinite.elms.dtos.UserDetailsResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public interface UserService {
    String register(UserDTO dto);

    UserDetailsResponseDTO getEmployeeById(Long id);

    List<UserDetailsResponseDTO> getAllEmployees();

    void deleteEmployee(Long id);

    UpdateEmployeeDTO updateEmployee(Long id, UpdateEmployeeDTO dto);
}
