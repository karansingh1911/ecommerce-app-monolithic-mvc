package com.karan.ecommerce_app.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePasswordRequestDTO {
    @NotBlank
    private String oldPassword;
    @NotBlank
    @Size(min = 8,message = "Password must be at least 8 characters")
    private String newPassword;
}
