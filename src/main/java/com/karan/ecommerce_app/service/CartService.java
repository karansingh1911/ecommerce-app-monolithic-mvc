package com.karan.ecommerce_app.service;

import com.karan.ecommerce_app.dto.cart.CartItemResponseDTO;
import com.karan.ecommerce_app.dto.cart.CartResponseDTO;
import com.karan.ecommerce_app.model.Cart;
import com.karan.ecommerce_app.model.CartItem;
import com.karan.ecommerce_app.model.Product;
import com.karan.ecommerce_app.repository.CartRepository;
import com.karan.ecommerce_app.repository.ProductRepository;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    CartRepository cartRepository;
    @Autowired
    ProductRepository productRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = "cartByUserId", key = "#userId")
    public CartResponseDTO getCart(Long userId) {
        Cart cart = cartRepository.findCartWithItemsAndProducts(userId).orElseThrow(() -> new RuntimeException(" Resource not found."));
        List<CartItemResponseDTO> itemList = cart.getCartItemsList().stream().map(item -> {
            return CartItemResponseDTO.builder().cartItemId(item.getCartItemId()).productName(item.getProduct().getProductName()).productId(item.getProduct().getProductId()).quantity(item.getQuantity()).unitPrice(item.getPriceAtTime()).build();
        }).toList();

        return CartResponseDTO.builder().cartId(cart.getCartId()).cartItemResponseDTOList(itemList).build();
    }

    @Transactional
    @CacheEvict(value = "cartByUserId", key = "#userId")
    public CartResponseDTO addToCart(Long productId, Integer quantity, Long userId) {

        // getting the product from the DB
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));


        // getting the cart for current user - no checks needed!
        Cart cart = cartRepository.findByUser_Id(userId).orElseThrow(() -> new RuntimeException("Resource not found."));

        // checking if there is any cartItem with the products or not
        Optional<CartItem> cartItemIfExists = cart.getCartItemsList().stream().filter((cartItem) -> {
            return cartItem.getProduct().getProductId().equals(productId);
        }).findFirst();
        if (cartItemIfExists.isEmpty()) { // productIfExists = null
            if (quantity > product.getStockQuantity()) {
                throw new RuntimeException("Insufficient stock");
            }
            CartItem newItem = new CartItem();
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setPriceAtTime(product.getPrice());
            cart.addItem(newItem);
        } else {
            //productIfExists = some value
            CartItem existingItem = cartItemIfExists.get();
            if (existingItem.getQuantity() + quantity > product.getStockQuantity()) {
                throw new RuntimeException("Insufficient stock");
            }
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        }
        // inventory check
        Cart savedCart = cartRepository.save(cart);
        List<CartItemResponseDTO> itemList = savedCart.getCartItemsList().stream().map(item -> CartItemResponseDTO.builder().cartItemId(item.getCartItemId()).productId(item.getProduct().getProductId()).productName(item.getProduct().getProductName()).quantity(item.getQuantity()).unitPrice(item.getPriceAtTime()).build()).toList();


        return CartResponseDTO.builder().cartId(savedCart.getCartId()).cartItemResponseDTOList(itemList).build();


    }

    @CacheEvict(value = "cartByUserId", key = "#userId")
    public void clearCart(Long userId) {
        // cart shouldn't be deleted rather cartItems should be cleared.
        Cart cart = cartRepository.findByUser_Id(userId).orElseThrow(() -> new RuntimeException("Resource not found."));

        cart.getCartItemsList().clear();

        cartRepository.save(cart);
    }

    @CacheEvict(value = "cartByUserId", key = "#userId")
    @Transactional
    public void deleteCartItem(Long cartItemId, Long userId) {

        //got the cart for the user
        Cart cart = cartRepository.findByUser_Id(userId).orElseThrow(() -> new RuntimeException("Resource not found."));
        CartItem itemToRemove = cart.getCartItemsList().stream().filter(item -> item.getCartItemId().equals(cartItemId)).findFirst().orElseThrow(() -> new RuntimeException("Cart item not found"));
        cart.removeItem(itemToRemove);
        cartRepository.save(cart);
    }

    @Transactional
    @CacheEvict(value = "cartByUserId", key = "#userId")
    public CartResponseDTO updateQuantityByCartItemId(Long cartItemId, Integer quantity, Long userId) {
        //get the cart for the user
        Cart cart = cartRepository.findByUser_Id(userId).orElseThrow(() -> new RuntimeException("Resource not found."));

        // find the cartItem with the index to be updated by quantity
        CartItem itemToBeUpdated = cart.getCartItemsList().stream().filter(item -> item.getCartItemId().equals(cartItemId)).findFirst().orElseThrow(() -> new RuntimeException("Resource not found "));

        //stock validation
        if (quantity > itemToBeUpdated.getProduct().getStockQuantity()) {
            throw new RuntimeException("Insufficient stock");
        }
        itemToBeUpdated.setQuantity(quantity);
        Cart savedCart = cartRepository.save(cart);
        List<CartItemResponseDTO> itemList = savedCart.getCartItemsList().stream().map(item -> CartItemResponseDTO.builder().cartItemId(item.getCartItemId()).productId(item.getProduct().getProductId()).productName(item.getProduct().getProductName()).quantity(item.getQuantity()).unitPrice(item.getPriceAtTime()).build()).toList();
        return CartResponseDTO.builder().cartId(savedCart.getCartId()).cartItemResponseDTOList(itemList).build();

    }
}
