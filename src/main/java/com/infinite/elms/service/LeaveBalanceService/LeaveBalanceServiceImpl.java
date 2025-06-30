package com.infinite.elms.service.LeaveBalanceService;

import com.infinite.elms.dtos.leaveBalanceDTO.LeaveBalanceDTO;
import com.infinite.elms.exception.customException.ResourceNotFoundException;
import com.infinite.elms.models.LeaveBalance;
import com.infinite.elms.repositories.LeaveBalanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaveBalanceServiceImpl implements LeaveBalanceService {

    private final LeaveBalanceRepository leaveBalanceRepository;

    @Override
    public LeaveBalanceDTO findLeaveBalanceByUserId(Long userId) {
        LeaveBalance leaveBalance = leaveBalanceRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave balance not found for user ID: " + userId));

        return LeaveBalanceDTO.builder()
                .userId(userId)
                .balances(leaveBalance.getBalances())
                .build();
    }
}
