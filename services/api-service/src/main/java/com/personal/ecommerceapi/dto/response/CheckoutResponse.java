package com.personal.ecommerceapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public class CheckoutResponse {
    private Long orderId;
    private BigDecimal totalAmount;
}
