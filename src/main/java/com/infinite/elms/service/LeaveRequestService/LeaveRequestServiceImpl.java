package com.infinite.elms.service.LeaveRequestService;
import com.infinite.elms.constants.LeaveStatus;
import com.infinite.elms.constants.LeaveType;
import com.infinite.elms.dtos.leaveRequestDTO.LeaveRequestDTO;
import com.infinite.elms.dtos.leaveRequestDTO.LeaveRequestResponseDTO;
import com.infinite.elms.exception.customException.InsufficientLeaveBalanceException;
import com.infinite.elms.exception.customException.LeaveRequestAlreadyReviewedException;
import com.infinite.elms.exception.customException.ResourceNotFoundException;
import com.infinite.elms.models.LeaveBalance;
import com.infinite.elms.models.LeaveRequest;
import com.infinite.elms.models.Users;
import com.infinite.elms.repositories.LeaveBalanceRepository;
import com.infinite.elms.repositories.LeaveRequestRepository;
import com.infinite.elms.repositories.UserRepository;
import com.infinite.elms.utils.ConvertToLeaveRequestResponseDTO;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class LeaveRequestServiceImpl implements LeaveRequestService {

    private final UserRepository userRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final ConvertToLeaveRequestResponseDTO convertToPendingLeaveDTOList;

    @Override
    public void submitLeaveRequest(String userEmail, LeaveRequestDTO dto) {
        log.info("Submitting leave request for user: {}", userEmail);
        Users employee = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

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
                .orElseThrow(() -> new ResourceNotFoundException("Leave request with ID " + requestId + " not found"));

        if (request.getStatus()!= LeaveStatus.PENDING) {
            log.warn("Leave request {} has already been reviewed", requestId);
            throw new LeaveRequestAlreadyReviewedException("Leave request already reviewed");
        }

        Users approver = userRepository.findByEmail(approverEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Approver not found"));

        request.setStatus(status);
        request.setApprover(approver);
        request.setDecisionComment(comment);
        request.setDecisionDate(LocalDate.now());

        if (status == LeaveStatus.APPROVED) {
            LeaveBalance balance = leaveBalanceRepository.findByUserId(request.getEmployee().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Leave balance for user ID " + request.getEmployee().getId() + " not found"));

            LeaveType type = request.getLeaveType();
            long days = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;
            int currentBalance = balance.getBalances().getOrDefault(type, 0);

            if (currentBalance < days) {
                log.warn("Insufficient balance: requested={} available={} type={}",
                        days, currentBalance, type);
                throw new InsufficientLeaveBalanceException("Insufficient leave balance");
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
        return  convertToPendingLeaveDTOList.convertToPendingLeaveDTOList(leaveRequests,"No Leave Requests Found");
    }

    @Override
    public List<LeaveRequestResponseDTO> findPendingRequest() {
        log.info("Fetching all pending leave requests");

        List<LeaveRequest> pendingRequests = leaveRequestRepository.getRequestByStatus(LeaveStatus.PENDING);
        return  convertToPendingLeaveDTOList.convertToPendingLeaveDTOList(pendingRequests,"No pending leave requests found");


    }

    @Override
    public List<LeaveRequestResponseDTO> findPendingRequestByUserId(Long userId) {
        log.info("Fetching pending leave requests By id {}", userId);
        List<LeaveRequest>  pendingRequests= leaveRequestRepository.findByEmployeeIdAndStatus(userId,LeaveStatus.PENDING);
        return  convertToPendingLeaveDTOList.convertToPendingLeaveDTOList(pendingRequests,"No pending leave requests found for userId " + userId);

    }
}



