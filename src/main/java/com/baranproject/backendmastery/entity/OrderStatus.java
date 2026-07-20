package com.baranproject.backendmastery.entity;

/**
 * Sipariş durumlarını temsil eden Java enum'ı.
 *
 * PostgreSQL'de bunu 'order_status' adında bir ENUM tipi olarak tanımladık.
 * Hibernate, Java enum değerlerini veritabanındaki ENUM değerleriyle eşleştirir.
 *
 * İş Akışı:
 *   PENDING → CONFIRMED → SHIPPED → DELIVERED
 *                ↓
 *            CANCELLED (herhangi bir noktada iptal edilebilir)
 */
public enum OrderStatus {
    PENDING,        // Sipariş alındı, henüz onaylanmadı
    CONFIRMED,      // Sipariş onaylandı
    SHIPPED,        // Kargoya verildi
    DELIVERED,      // Teslim edildi
    CANCELLED       // İptal edildi
}
