package com.personal.ecommerceapi.repository;

import com.personal.ecommerceapi.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUserId(Long userId);

    Optional<CartItem> findByUserIdAndProduct_Id(Long userId, Long productId);

    void deleteByUserIdAndProduct_Id(Long userId, Long productId);

    void deleteByUserId(Long userId);
}
