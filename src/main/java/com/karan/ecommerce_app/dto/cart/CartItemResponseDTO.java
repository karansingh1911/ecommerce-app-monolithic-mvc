package com.karan.ecommerce_app.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResponseDTO { // since cartItem has product we must extract product fields so that product doesn't get exposed

    private Long cartItemId;
    private Integer quantity;
    private BigDecimal unitPrice;
    //product fields
    private Long productId ;
    private String productName;


}
