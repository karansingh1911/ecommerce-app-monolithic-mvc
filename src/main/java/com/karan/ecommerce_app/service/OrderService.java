package com.karan.ecommerce_app.service;

import com.karan.ecommerce_app.dto.Order.OrderItemResponseDTO;
import com.karan.ecommerce_app.dto.Order.OrderRequestDTO;
import com.karan.ecommerce_app.dto.Order.OrderResponseDTO;
import com.karan.ecommerce_app.dto.Order.UpdateOrderStatusRequestDTO;
import com.karan.ecommerce_app.dto.ShippingAddress.ShippingAddressRequestDTO;
import com.karan.ecommerce_app.dto.ShippingAddress.ShippingAddressResponseDTO;
import com.karan.ecommerce_app.dto.cart.CartItemResponseDTO;
import com.karan.ecommerce_app.dto.cart.CartResponseDTO;
import com.karan.ecommerce_app.enums.OrderStatus;
import com.karan.ecommerce_app.model.Order;
import com.karan.ecommerce_app.model.OrderItem;
import com.karan.ecommerce_app.model.Product;
import com.karan.ecommerce_app.model.ShippingAddress;
import com.karan.ecommerce_app.repository.OrderRepository;
import com.karan.ecommerce_app.repository.ProductRepository;
import com.karan.ecommerce_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CartService cartService;
    @Autowired
    UserRepository userRepository;

    @Transactional
    @PreAuthorize("hasRole('USER')")
    @Caching(evict = {@CacheEvict(value = "ordersByUser", key = "#userId"), @CacheEvict(value = "orderPage", allEntries = true)})
    public OrderResponseDTO placeOrder(Long userId, OrderRequestDTO orderRequestDTO) {

        //get cart by userId
        CartResponseDTO cartResponseDTO = cartService.getCart(userId);

        // products from Db
        List<Product> products = productRepository.findAllById(cartResponseDTO.getCartItemResponseDTOList().stream().map(item -> item.getProductId()).toList());


        List<Long> availableProductIds = products.stream().map(item -> item.getProductId()).toList();
        List<Long> requestedProductIds = cartResponseDTO.getCartItemResponseDTOList().stream().map(item -> item.getProductId()).toList();
        if (!availableProductIds.containsAll(requestedProductIds) && products.size() != requestedProductIds.size()) {
            throw new RuntimeException("Some products requested don't exist");
        }

        // stock validation : O(n^2) - can be reduced to O(n) by using map<id,product>
        // validating requested product Ids, validating stock and creating List<OrderItem> in single traversal!
        Order order = new Order();
        List<OrderItem> orderItemList = new ArrayList<>();
        BigDecimal subTotal = BigDecimal.ZERO;
        for (CartItemResponseDTO item : cartResponseDTO.getCartItemResponseDTOList()) {
            for (Product product : products) {
                if (product.getProductId().equals(item.getProductId())) { // for certain id check for the stock - nested
                    if (product.getStockQuantity() < item.getQuantity()) {
                        throw new RuntimeException("Item is out of stock");
                    }
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setProductId(product.getProductId());
                    orderItem.setProductName(product.getProductName());
                    orderItem.setProductPrice(product.getPrice());
                    // deduction from inventory should happen simultaneously
                    orderItem.setQuantity(item.getQuantity());
                    product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
                    BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                    subTotal = subTotal.add(itemTotal);
                    orderItemList.add(orderItem);
                    break; // no need to  traverse any further since id is found at this point.

                }
            }
        }

        // setting remaining order fields using snapshot!
        order.setShippingAddress(orderRequestDTO.getShippingAddress());
        order.setUser(userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User-cart mismatch!")));
        order.setOrderStatus(OrderStatus.PAYMENT_PENDING);
        order.setSubTotal(subTotal);
        order.setOrderItems(orderItemList);
        order.setTotalAmount(subTotal); // at present using subTotal; i real system tax engine, delivery fee, coupons all
        // become separate modules

        Order savedOrder = orderRepository.save(order);
        // updating inventory in the product DB after order created - order of saving matters!
        productRepository.saveAll(products);

        // clear cart after order
        cartService.clearCart(userId);
        ShippingAddressResponseDTO shippingAddressResponseDTO = ShippingAddressResponseDTO.builder().fullName(savedOrder.getShippingAddress().getFullName()).phone(savedOrder.getShippingAddress().getPhone()).line1(savedOrder.getShippingAddress().getLine1()).line2(savedOrder.getShippingAddress().getLine2()).city(savedOrder.getShippingAddress().getCity()).state(savedOrder.getShippingAddress().getState()).pincode(savedOrder.getShippingAddress().getPincode()).build();
        List<OrderItemResponseDTO> orderItemResponseDTOList = savedOrder.getOrderItems().stream().map(orderItem -> OrderItemResponseDTO.builder().orderItemId(orderItem.getOrderItemId()).productId(orderItem.getProductId()).productName(orderItem.getProductName()).productPrice(orderItem.getProductPrice()).quantity(orderItem.getQuantity()).totalPrice(orderItem.getTotalPrice()).build()).toList();
        OrderResponseDTO orderResponseDTO = OrderResponseDTO.builder().orderId(savedOrder.getOrderId()).userId(savedOrder.getUser().getUserId()).items(orderItemResponseDTOList).subTotal(savedOrder.getSubTotal()).createdAt(savedOrder.getCreatedAt()).orderStatus(savedOrder.getOrderStatus()).shippingAddressResponseDTO(shippingAddressResponseDTO).build();
        return orderResponseDTO;
    }

    @PreAuthorize("hasRole('USER')")
    @Transactional(readOnly = true)
    @Cacheable(value = "ordersByUser", key = "#userId")
    public List<OrderResponseDTO> getMyOrders(Long userId) {

        // sorting latest orders first for better ecommerce UX
        List<Order> orderList = orderRepository.findAllByUser_UserIdOrderByCreatedAtDesc(userId);

        List<OrderResponseDTO> orderResponseDTOList = orderList.stream().map(order -> {

            ShippingAddressResponseDTO shippingAddressResponseDTO = ShippingAddressResponseDTO.builder().fullName(order.getShippingAddress().getFullName()).phone(order.getShippingAddress().getPhone()).line1(order.getShippingAddress().getLine1()).line2(order.getShippingAddress().getLine2()).city(order.getShippingAddress().getCity()).state(order.getShippingAddress().getState()).pincode(order.getShippingAddress().getPincode()).build();

            List<OrderItemResponseDTO> orderItemResponseDTOList = order.getOrderItems().stream().map(orderItem -> OrderItemResponseDTO.builder().orderItemId(orderItem.getOrderItemId()).productId(orderItem.getProductId()).productName(orderItem.getProductName()).productPrice(orderItem.getProductPrice()).quantity(orderItem.getQuantity()).totalPrice(orderItem.getTotalPrice()).build()).toList();

            return OrderResponseDTO.builder().orderId(order.getOrderId()).userId(order.getUser().getUserId()).items(orderItemResponseDTOList).subTotal(order.getSubTotal()).totalAmount(order.getTotalAmount()).orderStatus(order.getOrderStatus()).createdAt(order.getCreatedAt()).shippingAddressResponseDTO(shippingAddressResponseDTO).build();

        }).toList();

        return orderResponseDTOList;
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    @Caching(evict = {@CacheEvict(value = "orderById", key = "#orderId"), @CacheEvict(value = "ordersByUser", allEntries = true), @CacheEvict(value = "orderPage", allEntries = true)})
    public OrderResponseDTO updateOrderStatus(Long orderId, UpdateOrderStatusRequestDTO updateOrderStatusRequestDTO) {

        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));

        order.setOrderStatus(updateOrderStatusRequestDTO.getOrderStatus());

        Order savedOrder = orderRepository.save(order);

        ShippingAddressResponseDTO shippingAddressResponseDTO = ShippingAddressResponseDTO.builder().fullName(savedOrder.getShippingAddress().getFullName()).phone(savedOrder.getShippingAddress().getPhone()).line1(savedOrder.getShippingAddress().getLine1()).line2(savedOrder.getShippingAddress().getLine2()).city(savedOrder.getShippingAddress().getCity()).state(savedOrder.getShippingAddress().getState()).pincode(savedOrder.getShippingAddress().getPincode()).build();

        List<OrderItemResponseDTO> orderItemResponseDTOList = savedOrder.getOrderItems().stream().map(orderItem -> OrderItemResponseDTO.builder().orderItemId(orderItem.getOrderItemId()).productId(orderItem.getProductId()).productName(orderItem.getProductName()).productPrice(orderItem.getProductPrice()).quantity(orderItem.getQuantity()).totalPrice(orderItem.getTotalPrice()).build()).toList();

        return OrderResponseDTO.builder().orderId(savedOrder.getOrderId()).userId(savedOrder.getUser().getUserId()).items(orderItemResponseDTOList).subTotal(savedOrder.getSubTotal()).totalAmount(savedOrder.getTotalAmount()).createdAt(savedOrder.getCreatedAt()).orderStatus(savedOrder.getOrderStatus()).shippingAddressResponseDTO(shippingAddressResponseDTO).build();
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    @Cacheable(value = "orderById", key = "#orderId")
    public OrderResponseDTO getOrderByOrderId(Long orderId) {

        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));

        ShippingAddressResponseDTO shippingAddressResponseDTO = ShippingAddressResponseDTO.builder().fullName(order.getShippingAddress().getFullName()).phone(order.getShippingAddress().getPhone()).line1(order.getShippingAddress().getLine1()).line2(order.getShippingAddress().getLine2()).city(order.getShippingAddress().getCity()).state(order.getShippingAddress().getState()).pincode(order.getShippingAddress().getPincode()).build();

        List<OrderItemResponseDTO> orderItemResponseDTOList = order.getOrderItems().stream().map(orderItem -> OrderItemResponseDTO.builder().orderItemId(orderItem.getOrderItemId()).productId(orderItem.getProductId()).productName(orderItem.getProductName()).productPrice(orderItem.getProductPrice()).quantity(orderItem.getQuantity()).totalPrice(orderItem.getTotalPrice()).build()).toList();

        return OrderResponseDTO.builder().orderId(order.getOrderId()).userId(order.getUser().getUserId()).items(orderItemResponseDTOList).subTotal(order.getSubTotal()).totalAmount(order.getTotalAmount()).createdAt(order.getCreatedAt()).orderStatus(order.getOrderStatus()).shippingAddressResponseDTO(shippingAddressResponseDTO).build();
    }

    @Transactional
    @PreAuthorize("hasRole('USER')")
    @Caching(evict = {@CacheEvict(value = "orderById", key = "#orderId"), @CacheEvict(value = "ordersByUser", key = "#userId"), @CacheEvict(value = "orderPage", allEntries = true)})
    public ShippingAddressResponseDTO updateAddressBeforeShipping(Long userId, Long orderId, ShippingAddressRequestDTO shippingAddressRequestDTO) {

        Order order = orderRepository.findByOrderIdAndUser_UserId(orderId, userId).orElseThrow(() -> new RuntimeException("Order not found"));

        // Prevent modification after shipping
        if (order.getOrderStatus() == OrderStatus.ORDER_SHIPPED || order.getOrderStatus() == OrderStatus.ORDER_DELIVERED) {

            throw new RuntimeException("Address cannot be updated after shipping");
        }

        ShippingAddress shippingAddress = order.getShippingAddress();

        shippingAddress.setFullName(shippingAddressRequestDTO.getFullName());
        shippingAddress.setPhone(shippingAddressRequestDTO.getPhone());
        shippingAddress.setLine1(shippingAddressRequestDTO.getLine1());
        shippingAddress.setLine2(shippingAddressRequestDTO.getLine2());
        shippingAddress.setCity(shippingAddressRequestDTO.getCity());
        shippingAddress.setState(shippingAddressRequestDTO.getState());
        shippingAddress.setPincode(shippingAddressRequestDTO.getPincode());

        Order savedOrder = orderRepository.save(order);

        ShippingAddress updatedAddress = savedOrder.getShippingAddress();

        return ShippingAddressResponseDTO.builder().fullName(updatedAddress.getFullName()).phone(updatedAddress.getPhone()).line1(updatedAddress.getLine1()).line2(updatedAddress.getLine2()).city(updatedAddress.getCity()).state(updatedAddress.getState()).pincode(updatedAddress.getPincode()).build();
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    @Cacheable(value = "orderPage", key = "#page + '-' + #pageSize + '-createdAt-asc'")
    public Page<OrderResponseDTO> getAllOrders(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").ascending());
        Page<Order> orderPage = orderRepository.findAll(pageable);
        return orderPage.map(order -> {

            ShippingAddressResponseDTO shippingAddressResponseDTO = ShippingAddressResponseDTO.builder().fullName(order.getShippingAddress().getFullName()).phone(order.getShippingAddress().getPhone()).line1(order.getShippingAddress().getLine1()).line2(order.getShippingAddress().getLine2()).city(order.getShippingAddress().getCity()).state(order.getShippingAddress().getState()).pincode(order.getShippingAddress().getPincode()).build();

            List<OrderItemResponseDTO> orderItemResponseDTOList = order.getOrderItems().stream().map(orderItem -> OrderItemResponseDTO.builder().orderItemId(orderItem.getOrderItemId()).productId(orderItem.getProductId()).productName(orderItem.getProductName()).productPrice(orderItem.getProductPrice()).quantity(orderItem.getQuantity()).totalPrice(orderItem.getTotalPrice()).build()).toList();

            return OrderResponseDTO.builder().orderId(order.getOrderId()).userId(order.getUser().getUserId()).items(orderItemResponseDTOList).subTotal(order.getSubTotal()).totalAmount(order.getTotalAmount()).createdAt(order.getCreatedAt()).orderStatus(order.getOrderStatus()).shippingAddressResponseDTO(shippingAddressResponseDTO).build();

        });
    }
}

