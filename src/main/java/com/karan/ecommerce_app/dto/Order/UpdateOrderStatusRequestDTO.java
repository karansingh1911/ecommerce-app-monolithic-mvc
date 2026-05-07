package com.karan.ecommerce_app.dto.Order;

import com.karan.ecommerce_app.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateOrderStatusRequestDTO {

    @NotNull(message = "Order status is required")
    OrderStatus orderStatus;
}
