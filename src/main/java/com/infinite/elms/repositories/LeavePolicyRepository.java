package com.infinite.elms.repositories;

import com.infinite.elms.models.LeavePolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeavePolicyRepository extends JpaRepository<LeavePolicy, Long> {
    List<LeavePolicy> findByPolicyYear(Integer policyYear);
}
