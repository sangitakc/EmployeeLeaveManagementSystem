package com.infinite.elms.dtos;

import com.infinite.elms.constants.LeaveStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminDecisionDTO {
    @NotNull(message = "Status is required")
    private LeaveStatus status;

    @Size(max = 500, message = "Comment cannot exceed 500 characters")
    private String comment;
}
