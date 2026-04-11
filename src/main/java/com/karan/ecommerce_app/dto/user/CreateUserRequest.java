package com.karan.ecommerce_app.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class CreateUserRequest {
    @NotBlank
    private String firstName;
    private String lastName;
    @NotBlank
    private String password;
    private String phoneNumber;
    @Email
    @NotBlank
    private String email;

}
