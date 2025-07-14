package com.infinite.elms.service.UserService;
import com.infinite.elms.constants.LeaveType;
import com.infinite.elms.dtos.userDTO.UpdateEmployeeDTO;
import com.infinite.elms.dtos.userDTO.UserDTO;
import com.infinite.elms.dtos.userDTO.UserDetailsResponseDTO;
import com.infinite.elms.exception.customException.EmailAlreadyExistsException;
import com.infinite.elms.exception.customException.ResourceNotFoundException;
import com.infinite.elms.exception.customException.RoleNotFoundException;
import com.infinite.elms.models.LeaveBalance;
import com.infinite.elms.models.LeavePolicy;
import com.infinite.elms.models.Role;
import com.infinite.elms.models.Users;
import com.infinite.elms.repositories.LeaveBalanceRepository;
import com.infinite.elms.repositories.LeavePolicyRepository;
import com.infinite.elms.repositories.RoleRepository;
import com.infinite.elms.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository usersRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final LeavePolicyRepository leavePolicyRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;

    @Override
    public String register(UserDTO dto) {
        log.info("Registering user with email: {}", dto.getEmail());

        if (usersRepository.existsByEmail(dto.getEmail())) {
            log.error("Email already in use: {}", dto.getEmail());
            throw new EmailAlreadyExistsException("Email already in use");
        }

        Role defaultRole = roleRepository.findByName("EMPLOYEE").orElseThrow(() -> new RoleNotFoundException("Default role not found"));

        Users user = Users.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(defaultRole)
                .build();

        int currentYear = LocalDate.now().getYear();
        List<LeavePolicy> policies = leavePolicyRepository.findByPolicyYear(currentYear);

        if (policies.isEmpty()) {
            log.error("Leave policies not found for year {}", currentYear);
            throw new ResourceNotFoundException("Leave policies not configured for year " + currentYear);
        }

        Map<LeaveType, Integer> balanceMap = new HashMap<>();
        for (LeavePolicy policy : policies) {
            balanceMap.put(policy.getLeaveType(), policy.getAllowedDaysPerYear());
        }

        LeaveBalance leaveBalance = LeaveBalance.builder()
                .user(user)
                .balances(balanceMap)
                .build();

        LeaveBalance savedBalance = leaveBalanceRepository.save(leaveBalance);


        user.setLeaveBalance(savedBalance);
        usersRepository.save(user);
        log.info("User {} registered successfully with initial leave balances", dto.getEmail());
        return "User registered as EMPLOYEE and leave balance initialized";
    }


    @Override
    public List<UserDetailsResponseDTO> getAllEmployees() {
        List<Users> users = usersRepository.findAll();

        if (users.isEmpty()) {
            log.warn("No employees found in the system");
            throw new ResourceNotFoundException("No employees found");
        }

        log.info("Fetched {} employees", users.size());

        return users.stream()
                .map(user -> UserDetailsResponseDTO.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .roleName(user.getRole().getName())
                        .build())
                .toList();
    }

    @Override
    public void  deleteEmployee(Long id) {
        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));

        log.info("Deleting user with ID: {}", id);
        usersRepository.delete(user);
        log.info("User and related data deleted successfully for ID: {}", id);
    }

    @Override
    public UserDetailsResponseDTO getEmployeeById(Long id) {
        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));

        log.info("Fetched employee with ID: {}", id);

        return UserDetailsResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .roleName(user.getRole().getName())
                .build();
    }

    @Override
    public UpdateEmployeeDTO updateEmployee(Long id, UpdateEmployeeDTO dto) {
        log.info("Start: Updating employee with ID: {}", id);
        Users existingUser = usersRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Employee not found with ID: {}", id);
                    return new ResourceNotFoundException("Employee not found with ID: " + id);
                });

        if (dto.getName() != null && !dto.getName().isBlank()) {
            log.debug("Updating name to: {}", dto.getName());
            existingUser.setName(dto.getName());
        }

        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            if (!existingUser.getEmail().equals(dto.getEmail()) && usersRepository.existsByEmail(dto.getEmail())) {
                log.error("Email already in use: {}", dto.getEmail());
                throw new EmailAlreadyExistsException("Email already in use: " + dto.getEmail());
            }
            log.debug("Updating email to: {}", dto.getEmail());
            existingUser.setEmail(dto.getEmail());
        }

        if (dto.getRole() != null) {
            log.debug("Updating role to: {}", dto.getRole());
            Role role = roleRepository.findByName(dto.getRole())
                    .orElseThrow(() -> {
                        log.error("Role not found: {}", dto.getRole());
                        return new RoleNotFoundException("Role not found: " + dto.getRole());
                    });
            existingUser.setRole(role);
        }

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            log.debug("Updating password (encrypted)");
            existingUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        usersRepository.save(existingUser);
        log.info("Completed: Employee with ID {} updated successfully", id);

        return UpdateEmployeeDTO.builder()
                .name(existingUser.getName())
                .email(existingUser.getEmail())
                .role(existingUser.getRole().getName())
                .build();
    }
}

