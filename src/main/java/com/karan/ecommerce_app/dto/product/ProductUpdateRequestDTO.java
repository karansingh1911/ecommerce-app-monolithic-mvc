package com.karan.ecommerce_app.dto.product;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateRequestDTO {
    private String productName;
    private String productDescription;
    private String productCategory;
    private String productBrand;
    @PositiveOrZero
    private Integer stockQuantity;
    @Positive
    private BigDecimal price;

}
