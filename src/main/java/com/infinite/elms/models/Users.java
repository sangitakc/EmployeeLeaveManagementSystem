package com.infinite.elms.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.infinite.elms.utils.Auditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Table(name = "users")
public class Users extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @Column(unique = true)
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<LeaveRequest> leaveRequests = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private LeaveBalance leaveBalance;
}