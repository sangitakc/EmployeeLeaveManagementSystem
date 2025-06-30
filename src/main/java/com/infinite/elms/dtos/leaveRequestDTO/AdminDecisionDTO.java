package com.infinite.elms.dtos.leaveRequestDTO;
import com.infinite.elms.constants.LeaveStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminDecisionDTO {

    @NotNull(message = "Status is required")
    private LeaveStatus status;

    @NotNull(message = "Comment is required")
    private String comment;
}
