package com.infinite.elms.repositories;

import com.infinite.elms.models.LeaveRequest;
import com.infinite.elms.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByEmployee(User employee);
}
