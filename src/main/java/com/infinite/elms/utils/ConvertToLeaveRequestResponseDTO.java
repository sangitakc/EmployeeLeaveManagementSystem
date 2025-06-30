package com.infinite.elms.utils;

import com.infinite.elms.dtos.leaveRequestDTO.LeaveRequestResponseDTO;
import com.infinite.elms.models.LeaveRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class ConvertToLeaveRequestResponseDTO {

    public List<LeaveRequestResponseDTO> convertToPendingLeaveDTOList(List<LeaveRequest>  requests,String warnMessage) {
        if (requests.isEmpty()) {
            log.warn(warnMessage);
            return Collections.emptyList();
        }

        return requests.stream()
                .map(req ->LeaveRequestResponseDTO.builder()
                        .id(req.getId())
                        .startDate(req.getStartDate())
                        .endDate(req.getEndDate())
                        .status(req.getStatus())
                        .leaveType(req.getLeaveType())
                        .reason(req.getReason())
                        .employeeId(req.getEmployee().getId())
                        .employeeName(req.getEmployee().getName())
                        .requestDate(req.getRequestDate())
                        .build())
                .toList();
    }


}
