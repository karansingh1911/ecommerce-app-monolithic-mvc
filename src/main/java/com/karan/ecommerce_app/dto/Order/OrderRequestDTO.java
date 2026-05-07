package com.karan.ecommerce_app.dto.Order;

import com.karan.ecommerce_app.enums.OrderStatus;
import com.karan.ecommerce_app.model.OrderItem;
import com.karan.ecommerce_app.model.ShippingAddress;
import com.karan.ecommerce_app.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class OrderRequestDTO { // NOTE: what client can send?

    // client can only send the address the remaining details can be fetched from the Cart right?
    @NotNull
    private ShippingAddress shippingAddress;

}
