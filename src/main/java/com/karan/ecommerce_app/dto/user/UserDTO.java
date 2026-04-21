package com.karan.ecommerce_app.dto.user;

import com.karan.ecommerce_app.enums.Role;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Role role;
}
