package com.infinite.elms.controller;
import com.infinite.elms.dtos.leaveBalanceDTO.LeaveBalanceDTO;
import com.infinite.elms.service.LeaveBalanceService.LeaveBalanceService;
import com.infinite.elms.utils.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/leaveBalance")
@RequiredArgsConstructor
@Slf4j
public class LeaveBalanceController {
    private final LeaveBalanceService leaveBalanceService;

    @GetMapping("/getLeaveBalance/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<Response<LeaveBalanceDTO>> getLeaveBalanceByUserId(@PathVariable Long userId) {
        log.info("Request to get leave balance for user ID: {}", userId);
        LeaveBalanceDTO leaveBalanceDTO = leaveBalanceService.findLeaveBalanceByUserId(userId);
        return ResponseEntity.ok(
                Response.<LeaveBalanceDTO>builder()
                        .status(HttpStatus.OK.value())
                        .message("Leave balance fetched successfully")
                        .data(leaveBalanceDTO)
                        .build()
        );
    }
}
