package com.personal.ecommerceapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@Getter
public class CartResponse {
    private List<CartItemResponse> items;
    private BigDecimal total;
}
