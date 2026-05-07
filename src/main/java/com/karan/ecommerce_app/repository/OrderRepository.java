package com.karan.ecommerce_app.repository;

import com.karan.ecommerce_app.model.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"orderItems"})
    List<Order> findAllByUser_UserIdOrderByCreatedAtDesc(Long userId);

    Optional<Order> findByOrderIdAndUser_UserId(Long orderId, Long userId);
}
