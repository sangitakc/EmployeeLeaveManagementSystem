package com.infinite.elms.controller;
import com.infinite.elms.dtos.AdminDecisionDTO;
import com.infinite.elms.dtos.LeaveRequestDTO;
import com.infinite.elms.dtos.LeaveRequestResponseDTO;
import com.infinite.elms.service.LeaveRequestService;
import com.infinite.elms.utils.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/leaveRequest")
@RequiredArgsConstructor
@Slf4j
public class LeaveRequestManageController {

    private final LeaveRequestService leaveRequestService;

    @PostMapping("/submit")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<Response<String>> submitLeave(@Valid
                                                        @RequestBody LeaveRequestDTO leaveRequestDto,
                                                        Authentication authentication) {

        String email = authentication.getName();
        log.info("Submitting leave request for user: {}", email);
        leaveRequestService.submitLeaveRequest(email, leaveRequestDto);
        log.info("Leave request submitted for user: {}", email);

        return ResponseEntity.ok(
                Response.<String>builder()
                        .status(HttpStatus.OK.value())
                        .message("Leave request submitted successfully")
                        .data(null)
                        .build()
        );
    }

    @PatchMapping("/review/{requestId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response<String>> reviewLeaveRequest( @Valid
                                                                @RequestBody AdminDecisionDTO decisionDTO,
                                                                @PathVariable Long requestId,
                                                                Authentication authentication) {

        String approverEmail = authentication.getName();
        log.info("Reviewing leave request {} by admin: {}", requestId, approverEmail);

        leaveRequestService.reviewLeaveRequest(
                requestId,
                decisionDTO.getStatus(),
                decisionDTO.getComment(),
                approverEmail
        );
        log.info("Leave request {} reviewed with status: {}", requestId, decisionDTO.getStatus());
        return ResponseEntity.ok(
                Response.<String>builder()
                        .status(HttpStatus.OK.value())
                        .message("Leave request has been " + decisionDTO.getStatus().name().toLowerCase())
                        .data(null)
                        .build()
        );
    }

    @GetMapping("/getAllLeaveRequest")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response<List<LeaveRequestResponseDTO>>> getAllLeaveRequests() {
        log.info("Admin requested all leave requests");

        List<LeaveRequestResponseDTO> leaveRequests = leaveRequestService.getAllLeaveRequests();

        if (leaveRequests.isEmpty()) {
            log.info("No leave requests to return");
            return ResponseEntity.ok(
                    Response.<List<LeaveRequestResponseDTO>>builder()
                            .status(HttpStatus.OK.value())
                            .message("No leave requests found")
                            .data(Collections.emptyList())
                            .build()
            );
        }

        log.info("Returning {} leave requests", leaveRequests.size());

        return ResponseEntity.ok(
                Response.<List<LeaveRequestResponseDTO>>builder()
                        .status(HttpStatus.OK.value())
                        .message("Leave requests fetched successfully")
                        .data(leaveRequests)
                        .build()
        );
    }
}


