package com.infinite.elms.service.LeaveRequestService;

import com.infinite.elms.constants.LeaveStatus;
import com.infinite.elms.dtos.leaveRequestDTO.LeaveRequestDTO;
import com.infinite.elms.dtos.leaveRequestDTO.LeaveRequestResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface LeaveRequestService {

    void submitLeaveRequest(String UserEmail, LeaveRequestDTO leaveRequestDto);
    void reviewLeaveRequest(Long requestId, LeaveStatus status, String comment, String approverEmail);
    List<LeaveRequestResponseDTO> getAllLeaveRequests();
    List<LeaveRequestResponseDTO> findPendingRequest();
    List<LeaveRequestResponseDTO> findPendingRequestByUserId(Long requestId);


}
