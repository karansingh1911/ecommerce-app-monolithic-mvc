package com.karan.ecommerce_app.dto.user;

import com.karan.ecommerce_app.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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
