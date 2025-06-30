package com.infinite.elms.service.LeaveBalanceService;

import com.infinite.elms.dtos.leaveBalanceDTO.LeaveBalanceDTO;
import org.springframework.stereotype.Component;

@Component
public interface LeaveBalanceService {
    LeaveBalanceDTO findLeaveBalanceByUserId(Long userId);
}
