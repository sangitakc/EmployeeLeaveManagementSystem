package com.infinite.elms.repositories;

import com.infinite.elms.constants.LeaveStatus;
import com.infinite.elms.models.LeaveRequest;
import com.infinite.elms.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> getRequestByStatus(LeaveStatus leaveStatus);
//<<<<<<< HEAD
    LeaveRequest findTopByEmployeeOrderByRequestDateDesc(Users employee);

//=======

     List<LeaveRequest> findByEmployeeIdAndStatus(Long userId, LeaveStatus status);
//>>>>>>> f1b45993fcf241265297b824fb589558963623a2
}
