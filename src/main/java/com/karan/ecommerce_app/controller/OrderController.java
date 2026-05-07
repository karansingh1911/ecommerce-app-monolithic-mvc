package com.karan.ecommerce_app.controller;

import com.karan.ecommerce_app.dto.Order.OrderRequestDTO;
import com.karan.ecommerce_app.dto.Order.OrderResponseDTO;
import com.karan.ecommerce_app.dto.Order.UpdateOrderStatusRequestDTO;
import com.karan.ecommerce_app.dto.ShippingAddress.ShippingAddressRequestDTO;
import com.karan.ecommerce_app.dto.ShippingAddress.ShippingAddressResponseDTO;
import com.karan.ecommerce_app.model.CustomUserDetails;
import com.karan.ecommerce_app.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/orders")
@Validated
@RestController
public class OrderController {
    @Autowired
    OrderService orderService;

    // order creation being the root of the entire order lifecycle, so should be created first

    @PostMapping("placeOrder/my-order")
    public ResponseEntity<OrderResponseDTO> placeOrder(@AuthenticationPrincipal CustomUserDetails user, @Valid @RequestBody OrderRequestDTO orderRequestDTO) {
        return new ResponseEntity<>(orderService.placeOrder(user.getUserId(), orderRequestDTO), HttpStatus.CREATED);

    }

    @GetMapping("/me")
    public ResponseEntity<List<OrderResponseDTO>> getMyOrder(@AuthenticationPrincipal CustomUserDetails user) {
        return new ResponseEntity<>(orderService.getMyOrders(user.getUserId()), HttpStatus.OK);

    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(@PathVariable Long orderId, @Valid @RequestBody UpdateOrderStatusRequestDTO updateOrderRequestDTO) {
        return new ResponseEntity<>(orderService.updateOrderStatus(orderId, updateOrderRequestDTO), HttpStatus.OK);

    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrderByOrderId(@PathVariable Long orderId) {
        return new ResponseEntity<>(orderService.getOrderByOrderId(orderId), HttpStatus.OK);
    }


    @PatchMapping("/my-order/{orderId}/update-shipping-address")
    public ResponseEntity<ShippingAddressResponseDTO> updateAddressBeforeShipping(@AuthenticationPrincipal CustomUserDetails user, @PathVariable Long orderId, @Valid @RequestBody ShippingAddressRequestDTO shippingAddressRequestDTO) {
        return new ResponseEntity<>(orderService.updateAddressBeforeShipping(user.getUserId(), orderId, shippingAddressRequestDTO), HttpStatus.OK);

    }

    // this time will do pagination as per real-ecom ux and for practice
    @GetMapping("/all")
    public ResponseEntity<Page<OrderResponseDTO>> getAllOrders(@RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "5") int pageSize) {

        return new ResponseEntity<>(orderService.getAllOrders(page,pageSize),HttpStatus.OK);
    }


}
