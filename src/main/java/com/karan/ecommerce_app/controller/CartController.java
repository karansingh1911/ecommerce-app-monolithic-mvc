package com.karan.ecommerce_app.controller;

import com.karan.ecommerce_app.dto.cart.AddToCartDTO;
import com.karan.ecommerce_app.dto.cart.CartResponseDTO;
import com.karan.ecommerce_app.model.CustomUserDetails;
import com.karan.ecommerce_app.service.CartService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("myCart")
@RestController
@Validated
public class CartController {

    @Autowired
    CartService cartService;

    @GetMapping("/items")
    public ResponseEntity<CartResponseDTO> getCart(@AuthenticationPrincipal CustomUserDetails user) { //cart owns the
        // relationship and holds
        // user_id column
        return new ResponseEntity<>(cartService.getCart(user.getUserId()), HttpStatus.OK);
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponseDTO> addToCart(@Valid @RequestBody AddToCartDTO request,
                                                     @AuthenticationPrincipal CustomUserDetails user) {
        return new ResponseEntity<>(cartService.addToCart(request.getProductId(), request.getQuantity(), user.getUserId()), HttpStatus.CREATED);
    }

    @PatchMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponseDTO> updateQuantity(@Positive @PathVariable Long cartItemId,
                                                          @Valid @RequestBody Integer quantity,
                                                          @AuthenticationPrincipal CustomUserDetails user) {

        return new ResponseEntity<>(cartService.updateQuantityByCartItemId(cartItemId,quantity, user.getUserId()), HttpStatus.OK);

    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<Void> deleteCartItem(@Positive @PathVariable Long cartItemId,
                                               @AuthenticationPrincipal CustomUserDetails user) {
        cartService.deleteCartItem(cartItemId, user.getUserId());
        return ResponseEntity.noContent().build();

    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal CustomUserDetails user) {
        cartService.clearCart(user.getUserId());
        return ResponseEntity.noContent().build();

    }
}
