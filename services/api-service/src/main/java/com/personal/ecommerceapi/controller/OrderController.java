package com.personal.ecommerceapi.controller;

import com.personal.ecommerceapi.dto.request.CheckoutRequest;
import com.personal.ecommerceapi.dto.response.CheckoutResponse;
import com.personal.ecommerceapi.dto.response.OrderResponse;
import com.personal.ecommerceapi.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.personal.ecommerceapi.dto.request.PayOrderRequest;
import jakarta.validation.Valid;


import java.util.List;

@RestController
@RequestMapping("api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    private Long userId(HttpServletRequest request) {
        Object val = request.getAttribute("userId");
        if (val instanceof Long l) return l;
        if (val instanceof Integer i) return i.longValue();
        throw new IllegalStateException("Missing userId attribute (JWT not processed?)");
    }

    private String email(HttpServletRequest request) {
        Object val = request.getAttribute("email");
        return val == null ? null : val.toString();
    }

    @PostMapping("/checkout")
    public CheckoutResponse checkout(HttpServletRequest request,
                                     @RequestBody(required = false) CheckoutRequest body) {

        String shipping = body != null ? body.getShippingAddress() : null;

        String userEmail = email(request);
        if (userEmail == null || userEmail.isBlank()) {
            throw new IllegalStateException("Missing email attribute (JWT missing email claim?)");
        }

        return orderService.checkout(userId(request), userEmail, shipping);
    }

    @GetMapping
    public List<OrderResponse> myOrders(HttpServletRequest request) {
        return orderService.myOrders(userId(request));
    }

    @GetMapping("/{id}")
    public OrderResponse myOrderById(HttpServletRequest request, @PathVariable Long id) {
        return orderService.myOrderById(userId(request), id);
    }

    @PostMapping("/pay")
    public void pay(HttpServletRequest request, @Valid @RequestBody PayOrderRequest body) {
        orderService.pay(userId(request), body.getOrderId(), body.getAmount());
    }

}
