package com.karan.ecommerce_app.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ProductImageResponseDTO {
    private byte[] productImage;
    private String productImageType;
}
