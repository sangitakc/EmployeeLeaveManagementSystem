package com.infinite.elms.service.impl;


import com.infinite.elms.constants.LeaveStatus;
import com.infinite.elms.constants.LeaveType;
import com.infinite.elms.dtos.LeaveRequestDTO;
import com.infinite.elms.dtos.LeaveRequestResponseDTO;
import com.infinite.elms.models.LeaveBalance;
import com.infinite.elms.models.LeaveRequest;
import com.infinite.elms.models.Users;
import com.infinite.elms.repositories.LeaveBalanceRepository;
import com.infinite.elms.repositories.LeaveRequestRepository;
import com.infinite.elms.repositories.UserRepository;
import com.infinite.elms.service.LeaveRequestService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class LeaveRequestServiceImpl implements LeaveRequestService {

    private final UserRepository userRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;

    @Override
    public void submitLeaveRequest(String userEmail, LeaveRequestDTO dto) {
        log.info("Submitting leave request for user: {}", userEmail);
        Users employee = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Employee not found"));

        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            log.warn("Invalid date range: startDate={} endDate={}", dto.getStartDate(), dto.getEndDate());
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        LeaveRequest request = LeaveRequest.builder()
                .employee(employee)
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .leaveType(dto.getLeaveType())
                .reason(dto.getReason())
                .status(LeaveStatus.PENDING)
                .requestDate(LocalDate.now())
                .build();

        leaveRequestRepository.save(request);
        log.info("Leave request submitted for user: {}", userEmail);
    }

    @Override
    @Transactional
    public void reviewLeaveRequest(Long requestId, LeaveStatus status, String comment, String approverEmail) {
        log.info("Reviewing leave request ID: {} by approver: {}", requestId, approverEmail);
        LeaveRequest request = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Leave request not found"));

        if (request.getStatus()!= LeaveStatus.PENDING) {
            log.warn("Leave request {} has already been reviewed", requestId);
            throw new RuntimeException("Leave request already reviewed");
        }

        Users approver = userRepository.findByEmail(approverEmail)
                .orElseThrow(() -> new RuntimeException("Approver not found"));

        request.setStatus(status);
        request.setApprover(approver);
        request.setDecisionComment(comment);
        request.setDecisionDate(LocalDate.now());

        if (status == LeaveStatus.APPROVED) {
            LeaveBalance balance = leaveBalanceRepository.findByUserId(request.getEmployee().getId())
                    .orElseThrow(() -> new RuntimeException("Leave balance not found"));

            LeaveType type = request.getLeaveType();
            long days = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;
            int currentBalance = balance.getBalances().getOrDefault(type, 0);

            if (currentBalance < days) {
                log.warn("Insufficient balance: requested={} available={} type={}",
                        days, currentBalance, type);
                throw new RuntimeException("Insufficient leave balance");
            }

            balance.getBalances().put(type, currentBalance - (int) days);
            leaveBalanceRepository.save(balance);
            log.info("Leave approved and balance updated for user ID: {}", request.getEmployee().getId());
        }

        leaveRequestRepository.save(request);
        log.info("Leave request {} reviewed successfully", requestId);
    }

    @Override
    public List<LeaveRequestResponseDTO> getAllLeaveRequests() {
        log.info("Fetching all leave requests from database");
        List<LeaveRequest> leaveRequests = leaveRequestRepository.findAll();

        if (leaveRequests.isEmpty()) {
            log.warn("No leave requests found");
            return Collections.emptyList();
        }

        log.info("Found {} leave requests", leaveRequests.size());

        return leaveRequests.stream()
                .map(leaveRequest -> LeaveRequestResponseDTO.builder()
                        .id(leaveRequest.getId())
                        .startDate(leaveRequest.getStartDate())
                        .endDate(leaveRequest.getEndDate())
                        .status(leaveRequest.getStatus())
                        .leaveType(leaveRequest.getLeaveType())
                        .reason(leaveRequest.getReason())
                        .employeeId(leaveRequest.getEmployee().getId())
                        .employeeName(leaveRequest.getEmployee().getName())
                        .requestDate(leaveRequest.getRequestDate())
                        .decisionDate(leaveRequest.getDecisionDate())
                        .approverId(leaveRequest.getApprover() != null ? leaveRequest.getApprover().getId() : null)
                        .decisionComment(leaveRequest.getDecisionComment())
                        .build())
                .collect(Collectors.toList());
    }
}
