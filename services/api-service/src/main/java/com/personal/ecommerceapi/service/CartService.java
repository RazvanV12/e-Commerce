package com.personal.ecommerceapi.service;


import com.personal.ecommerceapi.dto.response.CartItemResponse;
import com.personal.ecommerceapi.dto.response.CartResponse;
import com.personal.ecommerceapi.entity.CartItem;
import com.personal.ecommerceapi.entity.Product;
import com.personal.ecommerceapi.repository.CartItemRepository;
import com.personal.ecommerceapi.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartService(CartItemRepository cartItemRepository, ProductRepository productRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public void addToCart(Long userId, Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        if (product.getStockQuantity() != null && product.getStockQuantity() < quantity) {
            throw new IllegalArgumentException("Not enough stock");
        }

        CartItem item = cartItemRepository.findByUserIdAndProduct_Id(userId, productId)
                .orElseGet(() -> {
                    CartItem ci = new CartItem();
                    ci.setUserId(userId);
                    ci.setProduct(product);
                    ci.setQuantity(0);
                    return ci;
                });

        int newQty = item.getQuantity() + quantity;

        if (product.getStockQuantity() != null && product.getStockQuantity() < newQty) {
            throw new IllegalArgumentException("Not enough stock");
        }

        item.setQuantity(newQty);
        cartItemRepository.save(item);
    }

    @Transactional(readOnly = true)
    public CartResponse getCart(Long userId) {
        List<CartItem> items = cartItemRepository.findByUserId(userId);

        List<CartItemResponse> respItems = items.stream().map(ci -> {
            Product p = ci.getProduct();
            BigDecimal unit = p.getPrice();
            BigDecimal line = unit.multiply(BigDecimal.valueOf(ci.getQuantity()));
            return new CartItemResponse(
                    p.getId(),
                    p.getName(),
                    p.getUrlToImage(),
                    ci.getQuantity(),
                    unit,
                    line
            );
        }).toList();

        BigDecimal total = respItems.stream()
                .map(CartItemResponse::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponse(respItems, total);
    }

    @Transactional
    public void updateQuantity(Long userId, Long productId, int quantity) {
        CartItem item = cartItemRepository.findByUserIdAndProduct_Id(userId, productId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));

        Product p = item.getProduct();
        if (p.getStockQuantity() != null && p.getStockQuantity() < quantity) {
            throw new IllegalArgumentException("Not enough stock");
        }

        item.setQuantity(quantity);
        cartItemRepository.save(item);
    }

    @Transactional
    public void removeItem(Long userId, Long productId) {
        cartItemRepository.deleteByUserIdAndProduct_Id(userId, productId);
    }

    @Transactional
    public void clearCart(Long userId) {
        cartItemRepository.deleteByUserId(userId);
    }
}