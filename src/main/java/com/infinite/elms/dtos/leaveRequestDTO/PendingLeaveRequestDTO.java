package com.infinite.elms.dtos.leaveRequestDTO;

import com.infinite.elms.constants.LeaveStatus;
import com.infinite.elms.constants.LeaveType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class PendingLeaveRequestDTO {
    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private LeaveStatus status;
    private LeaveType leaveType;
    private String reason;
    private Long employeeId;
    private String employeeName;
    private LocalDate requestDate;
}
