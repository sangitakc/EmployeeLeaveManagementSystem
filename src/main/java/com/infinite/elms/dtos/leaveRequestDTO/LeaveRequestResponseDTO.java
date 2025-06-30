package com.infinite.elms.dtos.leaveRequestDTO;
import com.infinite.elms.constants.LeaveStatus;
import com.infinite.elms.constants.LeaveType;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequestResponseDTO {
    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private LeaveStatus status;
    private LeaveType leaveType;
    private String reason;
    private Long employeeId;
    private String employeeName;
    private LocalDate requestDate;
    private LocalDate decisionDate;
    private Long approverId;
    private String decisionComment;
}
