package com.infinite.elms.dtos.leaveBalanceDTO;

import com.infinite.elms.constants.LeaveType;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class LeaveBalanceDTO {
    private Long userId;
    private Map<LeaveType, Integer> balances;
}