package com.baranproject.backendmastery.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Product Entity — 'products' tablosunu temsil eder.
 *
 * === KULLANILAN ANOTASYONLAR ===
 *
 * @Entity → Bu sınıfın bir JPA entity'si (veritabanı tablosu) olduğunu
 *         belirtir.
 *         Hibernate bu sınıfı veritabanındaki bir tablo ile eşleştirir.
 *
 * @Table → Tablo adını açıkça belirtir. Yazmazsak sınıf adı kullanılır.
 *        Best practice: her zaman açıkça yaz.
 *
 * @Getter/@Setter → Lombok: tüm alanlar için getter/setter metodlarını otomatik
 *                 üretir.
 *                 Böylece 50 satır boilerplate kod yazmaktan kurtuluruz.
 *
 * @NoArgsConstructor → Lombok: parametresiz constructor üretir.
 *                    JPA spesifikasyonu bunu ZORUNLU kılar (reflection ile
 *                    nesne oluşturur).
 *
 * @AllArgsConstructor → Lombok: tüm alanları parametre alan constructor üretir.
 *                     Test yazarken veya Builder pattern'de kullanışlıdır.
 *
 * @Builder → Lombok: Builder pattern ile nesne oluşturmayı sağlar.
 *          Örnek: Product.builder().name("Laptop").price(...).build();
 */
@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    /**
     * @Id → Bu alanın Primary Key olduğunu belirtir.
     * @GeneratedValue → Değerin otomatik üretileceğini belirtir.
     *                 GenerationType.IDENTITY → Veritabanının auto-increment
     *                 özelliğini kullan
     *                 (PostgreSQL'de BIGSERIAL).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * @Column → Sütun özelliklerini belirtir.
     *         nullable = false → SQL'deki NOT NULL kısıtlaması
     *         length = 255 → VARCHAR(255) ile eşleşir
     *
     *         NOT: Flyway zaten bu kısıtlamaları SQL'de tanımladı.
     *         Buradaki @Column, JPA seviyesinde doğrulama ve dokümantasyon
     *         amaçlıdır.
     */
    @Column(nullable = false, length = 255)
    private String name;

    /**
     * columnDefinition = "TEXT" → PostgreSQL TEXT tipini kullan.
     * VARCHAR yerine TEXT, sınırsız uzunlukta metin saklar.
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * BigDecimal: Para hesaplamalarında ZORUNLU!
     * Double/Float kullanma → 0.1 + 0.2 = 0.30000000000000004 gibi hatalar verir.
     * BigDecimal → tam hassasiyet sağlar.
     *
     * precision = 10, scale = 2 → DECIMAL(10,2) ile eşleşir
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity = 0;

    /**
     * Ürün görselinin MinIO/S3'teki URL'i.
     * Nullable — ürün resimsiz de eklenebilir.
     */
    @Column(name = "image_url", length = 512)
    private String imageUrl;

    /**
     * @CreationTimestamp → Hibernate, entity ilk kaydedildiğinde
     *                    bu alana otomatik olarak şu anki zamanı yazar.
     *                    Manuel set etmeye gerek yok!
     *
     *                    updatable = false → Bu alan sonradan güncellenemez
     *                    (oluşturma tarihi sabit kalır).
     *
     *                    OffsetDateTime → Saat dilimi bilgisini de tutar (TIMESTAMP
     *                    WITH TIME ZONE).
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    /**
     * @UpdateTimestamp → Hibernate, entity her güncellendiğinde
     *                  bu alana otomatik olarak şu anki zamanı yazar.
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
