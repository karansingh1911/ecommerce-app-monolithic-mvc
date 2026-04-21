package com.karan.ecommerce_app.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PostProductRequestDTO {
    @NotBlank
    private String productName;
    @NotBlank
    private String productDescription;
    @NotBlank
    private String productCategory;
    @NotBlank
    private String productBrand;
    @Positive
    @NotNull
    private BigDecimal price;
    @NotNull
    @PositiveOrZero
    private Integer stockQuantity;
}
