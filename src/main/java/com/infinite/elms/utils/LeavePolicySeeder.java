package com.infinite.elms.utils;

import com.infinite.elms.constants.LeaveType;
import com.infinite.elms.models.LeavePolicy;
import com.infinite.elms.repositories.LeavePolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
@Component
@RequiredArgsConstructor
public class LeavePolicySeeder implements CommandLineRunner {

    private final LeavePolicyRepository leavePolicyRepository;

    @Override
    public void run(String... args) throws Exception {
        int year = LocalDate.now().getYear();

        if (!leavePolicyRepository.findByPolicyYear(year).isEmpty()) return;

        List<LeavePolicy> policies = List.of(
                new LeavePolicy(null, LeaveType.SICK_LEAVE, 10, year),
                new LeavePolicy(null, LeaveType.CASUAL_LEAVE, 12, year),
                new LeavePolicy(null, LeaveType.PAID_LEAVE, 15, year),
                new LeavePolicy(null, LeaveType.UNPAID_LEAVE, 9999, year),
                new LeavePolicy(null, LeaveType.MATERNITY_LEAVE, 90, year)
        );

        leavePolicyRepository.saveAll(policies);
        System.out.println("LeavePolicy seeded for year " + year);
    }
}

