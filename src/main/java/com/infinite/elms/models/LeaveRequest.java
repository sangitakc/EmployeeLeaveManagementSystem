package com.infinite.elms.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.infinite.elms.constants.LeaveStatus;
import com.infinite.elms.constants.LeaveType;
import com.infinite.elms.utils.Auditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "leave_requests")
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class LeaveRequest extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private LeaveStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveType leaveType;

    @NotBlank
    private String reason;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    @JsonBackReference
    private Users employee;

    private LocalDate requestDate;

    private LocalDate decisionDate;

    @ManyToOne
    @JoinColumn(name = "approver_id")
    private Users approver;

    private String decisionComment;
}