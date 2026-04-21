package com.karan.ecommerce_app.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PostProductResponseDTO {
    private long productId;
    private String productName;
    private String productDescription;
    private String productCategory;
    private String productBrand;
    private BigDecimal price;
    private Boolean isAvailable;
    private Integer stockQuantity;
    private LocalDateTime productReleaseDate;
    private LocalDateTime productUpdateDate;

}
