package com.infinite.elms.models;

import com.infinite.elms.constants.LeaveStatus;
import com.infinite.elms.constants.LeaveType;
import com.infinite.elms.utils.Auditable;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "leave_requests")
@Builder
public class LeaveRequest extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate startDate;

    private LocalDate requestDate;

    private LocalDate endDate;

    private LocalDate decisionDate;

    private String decisionComment;

    @Enumerated(EnumType.STRING)
    private LeaveStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveType leaveType;

    private String reason;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Users employee;

    @ManyToOne
    @JoinColumn(name = "approver_id")
    private Users approver;
}
