package com.infinite.elms.service;

import com.infinite.elms.constants.LeaveStatus;
import com.infinite.elms.dtos.LeaveRequestDTO;
import com.infinite.elms.dtos.LeaveRequestResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface LeaveRequestService {

    void submitLeaveRequest(String UserEmail, LeaveRequestDTO leaveRequestDto);
    void reviewLeaveRequest(Long requestId, LeaveStatus status, String comment, String approverEmail);
    List<LeaveRequestResponseDTO> getAllLeaveRequests();

}
