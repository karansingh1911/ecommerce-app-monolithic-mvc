package com.karan.ecommerce_app.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserResponseDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

}
