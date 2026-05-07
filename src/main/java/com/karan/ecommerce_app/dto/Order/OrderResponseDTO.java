package com.karan.ecommerce_app.dto.Order;

import com.karan.ecommerce_app.dto.ShippingAddress.ShippingAddressResponseDTO;
import com.karan.ecommerce_app.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class OrderResponseDTO {


    private Long orderId;
    private Long userId; // flattened
    private List<OrderItemResponseDTO> items;
    private BigDecimal subTotal;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private OrderStatus orderStatus;
    private ShippingAddressResponseDTO shippingAddressResponseDTO;

}
