package com.baranproject.backendmastery.repository;

import com.baranproject.backendmastery.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * OrderItem Repository — Sipariş kalemleri veritabanı erişim katmanı.
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // Şu an ekstra metod gerekmiyor.
    // OrderItem'lara genelde Order üzerinden (order.getItems()) erişiriz.
}
