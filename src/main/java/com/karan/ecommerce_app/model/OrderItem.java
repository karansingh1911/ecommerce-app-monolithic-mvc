package com.karan.ecommerce_app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "order_items")
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id",nullable = false)
    private Order order;

    @NotNull
    @Column(nullable = false)
    private Long productId;
    @NotBlank
    @Column(nullable = false)
    private String productName;
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(nullable = false,precision = 10, scale = 2)
    private BigDecimal productPrice;
    @Positive
    @Column(nullable = false)
    private Integer quantity;
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(nullable = false,precision = 10, scale = 2)
    private BigDecimal totalPrice;


    @PreUpdate
    @PrePersist // whenever orderItem created or updated, total price will change
    public void calculateTotal() {
        if (productPrice != null && quantity != null) {
            this.totalPrice = productPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }
}


