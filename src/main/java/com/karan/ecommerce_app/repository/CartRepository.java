package com.karan.ecommerce_app.repository;

import com.karan.ecommerce_app.dto.cart.CartResponseDTO;
import com.karan.ecommerce_app.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    // NOTE: findBy works through property traversal and not by Column names//
    @Query("""
               SELECT c
               FROM Cart c
               JOIN FETCH c.cartItemsList ci
               JOIN FETCH ci.product
               WHERE c.user.id = :userId
            """)
    Optional<Cart> findCartWithItemsAndProducts(Long userId);

    Optional<Cart> findByUser_Id(Long userId);

}
