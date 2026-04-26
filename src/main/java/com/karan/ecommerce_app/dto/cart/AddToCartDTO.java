package com.karan.ecommerce_app.dto.cart;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddToCartDTO {

    @Positive
    private Long productId;
    @Positive
    private Integer quantity;
}
