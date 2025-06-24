package com.infinite.elms.models;

import com.infinite.elms.constants.LeaveType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "leave_policy")
@Builder
public class LeavePolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private LeaveType leaveType;

    @Column(nullable = false)
    private Integer allowedDaysPerYear;

    @Column(nullable = false)
    private Integer policyYear;
}
