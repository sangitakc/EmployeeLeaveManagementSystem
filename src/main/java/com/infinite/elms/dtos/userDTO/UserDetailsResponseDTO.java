package com.infinite.elms.dtos.userDTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDetailsResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String roleName;
}
