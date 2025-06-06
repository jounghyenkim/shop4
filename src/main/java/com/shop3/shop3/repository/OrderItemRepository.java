package com.shop3.shop3.repository;

import com.shop3.shop3.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem,Long> {
}
