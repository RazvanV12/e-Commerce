package com.personal.ecommerceapi.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class PayOrderRequest {
    @NotNull
    private Long orderId;

    @NotNull
    private BigDecimal amount;
}
