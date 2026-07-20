package com.baranproject.backendmastery.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * OrderItem Entity — 'order_items' tablosunu temsil eder.
 *
 * Her sipariş kalemi, bir siparişe (Order) ve bir ürüne (Product) bağlıdır.
 * Bu tablo, siparişin detaylarını tutar:
 *   "Hangi üründen kaç adet, hangi fiyattan alındı?"
 */
@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * @ManyToOne → Birden fazla OrderItem, tek bir Order'a ait olabilir.
     *
     * @JoinColumn(name = "order_id") → order_items tablosundaki 'order_id' sütununu
     *   kullanarak orders tablosuna foreign key bağlantısı kurar.
     *
     * fetch = FetchType.LAZY → Order bilgisi sadece erişildiğinde yüklenir.
     *   Performans açısından ManyToOne'da da LAZY kullanmak best practice'tir.
     *
     * nullable = false → Her order item mutlaka bir siparişe ait olmalı.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /**
     * @ManyToOne → Birden fazla OrderItem aynı Product'ı referans edebilir.
     *
     * Bu ilişki tek yönlüdür (unidirectional):
     *   OrderItem → Product yönünde gidebilirsin.
     *   Product → OrderItem yönünde bir alan yok
     *   (çünkü genelde "bu ürün hangi siparişlerde var?" sorusunu sorguyla çözeriz,
     *    entity'ye eklemek gereksiz karmaşıklık yaratır).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    /**
     * Sipariş anındaki birim fiyat.
     * Neden ayrı tutuyoruz? Çünkü ürün fiyatı sonradan değişebilir
     * ama müşterinin ödediği fiyat sabit kalmalıdır.
     * Bu, e-ticaret sistemlerinde temel bir kuraldır.
     */
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
