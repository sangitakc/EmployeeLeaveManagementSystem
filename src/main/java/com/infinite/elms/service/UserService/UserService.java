package com.infinite.elms.service.UserService;
import com.infinite.elms.dtos.userDTO.UpdateEmployeeDTO;
import com.infinite.elms.dtos.userDTO.UserDTO;
import com.infinite.elms.dtos.userDTO.UserDetailsResponseDTO;
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
