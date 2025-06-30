package com.infinite.elms.dtos.userDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
<<<<<<< HEAD:src/main/java/com/infinite/elms/dtos/UserDTO.java
import jakarta.validation.constraints.Size;
import lombok.Builder;
=======
>>>>>>> f1b45993fcf241265297b824fb589558963623a2:src/main/java/com/infinite/elms/dtos/userDTO/UserDTO.java
import lombok.Data;
@Builder
@Data
public class UserDTO {

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Email format is invalid")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

}

