package com.infinite.elms.repositories;

import com.infinite.elms.models.LeavePolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeavePolicyRepository extends JpaRepository<LeavePolicy, Long> {
    List<LeavePolicy> findByPolicyYear(Integer policyYear);
}
