package com.baranproject.backendmastery.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Order Entity — 'orders' tablosunu temsil eder.
 *
 * Bir siparişin birden fazla kalemi (OrderItem) olabilir → One-to-Many ilişki.
 */
@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_name", nullable = false, length = 255)
    private String customerName;

    @Column(name = "customer_email", nullable = false, length = 255)
    private String customerEmail;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    /**
     * @Enumerated(EnumType.STRING) → Enum değerini veritabanına STRING olarak kaydeder.
     *
     * İki seçenek vardır:
     *   - EnumType.ORDINAL → Sıra numarasını kaydeder (0, 1, 2...) — TEHLİKELİ!
     *     Eğer enum'a yeni bir değer eklenirse, eski verilerin anlamı değişir.
     *   - EnumType.STRING  → Değerin adını kaydeder ("PENDING", "CONFIRMED"...) — GÜVENLİ!
     *     Sıra değişse bile veri bütünlüğü korunur.
     *
     * columnDefinition = "order_status" → PostgreSQL'deki custom ENUM tipini kullan.
     */
    @Enumerated(EnumType.STRING)
    @org.hibernate.annotations.JdbcType(org.hibernate.dialect.PostgreSQLEnumJdbcType.class)
    @Column(nullable = false, columnDefinition = "order_status")
    private OrderStatus status = OrderStatus.PENDING;

    /**
     * === ONE-TO-MANY İLİŞKİ ===
     *
     * @OneToMany → Bir Order'ın birden fazla OrderItem'ı olabilir.
     *
     * mappedBy = "order" → İlişkinin "sahibi" OrderItem tarafındaki 'order' alanıdır.
     *   JPA'da her ilişkinin bir "sahibi" (owning side) ve bir "ters tarafı" (inverse side) vardır.
     *   Foreign key, OrderItem (order_items) tablosundadır → OrderItem sahip taraf.
     *   mappedBy diyerek "FK bende değil, karşı tarafta" demiş oluyoruz.
     *
     * cascade = CascadeType.ALL → Order kaydedilirken/silinirken,
     *   bağlı OrderItem'lar da otomatik kaydedilir/silinir.
     *   - PERSIST: Order save → item'lar da save
     *   - MERGE:   Order update → item'lar da update
     *   - REMOVE:  Order delete → item'lar da delete
     *
     * orphanRemoval = true → Order'ın items listesinden çıkarılan bir item,
     *   veritabanından da silinir (yetim kalmasın).
     *
     * fetch = FetchType.LAZY → İlişkili veriler SADECE erişildiğinde yüklenir.
     *   EAGER olsaydı, her Order sorgulandığında TÜM item'lar da yüklenirdi → performans kaybı.
     *   LAZY = "İhtiyaç olduğunda getir" → Best practice!
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    // ============================================================
    // Yardımcı Metod (Convenience Method)
    // ============================================================
    // İlişkilerde her iki tarafı da senkronize tutmak önemlidir.
    // Sadece items.add(item) yapmak yetmez, item'ın da order'ını set etmek gerekir.
    // Bu metod her iki tarafı da tek adımda günceller.

    /**
     * Siparişe yeni bir kalem ekler ve çift yönlü ilişkiyi senkronize eder.
     */
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }
}
