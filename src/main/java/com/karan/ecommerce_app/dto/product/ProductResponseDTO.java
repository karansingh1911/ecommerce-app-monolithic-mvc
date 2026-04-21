package com.karan.ecommerce_app.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
public class ProductResponseDTO {
    private long productId;
    private String productName;
    private String productDescription; // maybe trimmed
    private String productCategory;
    private String productBrand;
    private BigDecimal price;
    private boolean isAvailable;
}
