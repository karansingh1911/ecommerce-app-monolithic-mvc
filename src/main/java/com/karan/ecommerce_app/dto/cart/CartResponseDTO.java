package com.karan.ecommerce_app.dto.cart;

import com.karan.ecommerce_app.model.CartItem;
import com.karan.ecommerce_app.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@Getter
@AllArgsConstructor
public class CartResponseDTO {
    private Long cartId;
    private List<CartItemResponseDTO> cartItemResponseDTOList;
}
