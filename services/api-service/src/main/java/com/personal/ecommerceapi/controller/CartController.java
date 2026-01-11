package com.personal.ecommerceapi.controller;

import com.personal.ecommerceapi.dto.request.AddToCartRequest;
import com.personal.ecommerceapi.dto.request.UpdateCartItemQuantityRequest;
import com.personal.ecommerceapi.dto.response.CartResponse;
import com.personal.ecommerceapi.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    private Long userId(HttpServletRequest request) {
        Object val = request.getAttribute("userId");
        if (val instanceof Long l) return l;
        if (val instanceof Integer i) return i.longValue();
        throw new IllegalStateException("Missing userId attribute (JWT not processed?)");
    }

    // POST /cart/items  adaugare produse in cos
    @PostMapping("/items")
    public ResponseEntity<Void> addToCart(HttpServletRequest request,
                                          @Valid @RequestBody AddToCartRequest body) {
        cartService.addToCart(userId(request), body.getProductId(), body.getQuantity());
        return ResponseEntity.ok().build();
    }

    // GET /cart returnare cos (items si total)
    @GetMapping
    public CartResponse getCart(HttpServletRequest request) {
        return cartService.getCart(userId(request));
    }

    // PATCH /cart/items/{productId} setare cantitate
    @PatchMapping("/items/{productId}")
    public ResponseEntity<Void> updateQuantity(HttpServletRequest request,
                                               @PathVariable Long productId,
                                               @Valid @RequestBody UpdateCartItemQuantityRequest body) {
        cartService.updateQuantity(userId(request), productId, body.getQuantity());
        return ResponseEntity.ok().build();
    }

    // DELETE /cart/items/{productId} stergere produs din cos
    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void> removeItem(HttpServletRequest request, @PathVariable Long productId) {
        cartService.removeItem(userId(request), productId);
        return ResponseEntity.noContent().build();
    }

    // DELETE /cart/clear golire cos
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clear(HttpServletRequest request) {
        cartService.clearCart(userId(request));
        return ResponseEntity.noContent().build();
    }
}
