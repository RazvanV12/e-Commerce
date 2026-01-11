package com.personal.ecommerceapi.service;

import com.personal.ecommerceapi.dto.response.CheckoutResponse;
import com.personal.ecommerceapi.dto.response.OrderItemResponse;
import com.personal.ecommerceapi.dto.response.OrderResponse;
import com.personal.ecommerceapi.entity.CartItem;
import com.personal.ecommerceapi.entity.Order;
import com.personal.ecommerceapi.entity.OrderItem;
import com.personal.ecommerceapi.entity.Product;
import com.personal.ecommerceapi.repository.CartItemRepository;
import com.personal.ecommerceapi.repository.OrderRepository;
import com.personal.ecommerceapi.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import com.personal.ecommerceapi.dto.request.PayOrderRequest;
import com.personal.ecommerceapi.util.OrderStatus;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Transactional
    public CheckoutResponse checkout(Long userId, String email, String shippingAddress) {
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

       //verificare stoc
        for (CartItem ci : cartItems) {
            Product p = ci.getProduct();
            Integer stock = p.getStockQuantity();
            if (stock != null && stock < ci.getQuantity()) {
                throw new IllegalArgumentException("Not enough stock for product " + p.getId());
            }
        }

       //comanda
        Order order = new Order();
        order.setUserId(userId);
        order.setEmail(email);
        order.setShippingAddress(shippingAddress);

        BigDecimal total = BigDecimal.ZERO;

        //items + total + scadere stoc
        for (CartItem ci : cartItems) {
            Product p = ci.getProduct();
            BigDecimal unit = p.getPrice();
            BigDecimal line = unit.multiply(BigDecimal.valueOf(ci.getQuantity()));
            total = total.add(line);

            OrderItem oi = new OrderItem();
            oi.setProduct(p);
            oi.setQuantity(ci.getQuantity());
            oi.setUnitPrice(unit);

            order.addItem(oi);

            if (p.getStockQuantity() != null) {
                p.setStockQuantity(p.getStockQuantity() - ci.getQuantity());
                productRepository.save(p);
            }
        }

        order.setTotalAmount(total);

       //salvam order + golire cos
        Order saved = orderRepository.save(order);

        cartItemRepository.deleteByUserId(userId);

        return new CheckoutResponse(saved.getId(), saved.getTotalAmount());
    }
    @Transactional
    public void pay(Long userId, Long orderId, java.math.BigDecimal amount) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalArgumentException("Order is not in PENDING state");
        }

        if (order.getTotalAmount() == null || order.getTotalAmount().compareTo(amount) != 0) {
            throw new IllegalArgumentException("Invalid amount. Expected: " + order.getTotalAmount());
        }

        order.setStatus(OrderStatus.PAID);
        order.setPaidAt(LocalDateTime.now());
        orderRepository.save(order);
    }


    @Transactional
    public void autoShipIfDue(Order order) {
        if (order.getStatus() == OrderStatus.PAID && order.getPaidAt() != null) {
            if (order.getPaidAt().isBefore(LocalDateTime.now().minusMinutes(1))) {
                order.setStatus(OrderStatus.SHIPPED);
                orderRepository.save(order);
            }
        }
    }


    @Transactional(readOnly = false)
    public List<OrderResponse> myOrders(Long userId) {
        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);

        for (Order o : orders) {
            autoShipIfDue(o);
        }

        return orders.stream().map(this::toResponse).toList();
    }


    @Transactional(readOnly = false)
    public OrderResponse myOrderById(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        autoShipIfDue(order);

        return toResponse(order);
    }


    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream().map(oi -> {
            Product p = oi.getProduct();
            BigDecimal line = oi.getUnitPrice().multiply(BigDecimal.valueOf(oi.getQuantity()));
            return new OrderItemResponse(
                    p.getId(),
                    p.getName(),
                    p.getUrlToImage(),
                    oi.getQuantity(),
                    oi.getUnitPrice(),
                    line
            );
        }).toList();

        return new OrderResponse(
                order.getId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getShippingAddress(),
                order.getCreatedAt(),
                items
        );
    }
}