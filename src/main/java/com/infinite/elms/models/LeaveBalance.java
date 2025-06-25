package com.infinite.elms.models;

import com.infinite.elms.constants.LeaveType;
import com.infinite.elms.utils.Auditable;
import jakarta.persistence.*;
import lombok.*;

import java.util.Map;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "leave_balances")
@Builder
public class LeaveBalance extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private Users user;

    @ElementCollection
    @CollectionTable(name = "leave_balances_per_employee", joinColumns = @JoinColumn(name = "leave_balance_id"))
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "leave_type")
    @Column(name = "balance")
    private Map<LeaveType, Integer> balances;

}
