package com.personal.ecommerceapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@Getter
public class CartItemResponse {
    private Long productId;
    private String name;
    private String imageUrl;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;

    public CartItemResponse(List<CartItemResponse> respItems, BigDecimal total) {
    }
}
