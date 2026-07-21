package com.baranproject.backendmastery.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Sipariş kalemi DTO'su.
 *
 * Entity'deki OrderItem'dan farkları:
 *   - "Order order" yok → onun yerine hiçbir şey yok (siparişin parçası olarak gönderilecek)
 *   - "Product product" yok → onun yerine sadece productId var (ilişki nesnesi değil, ID)
 *   - "createdAt" yok → sistem yönetir
 *   - "productName" eklendi → kullanıcıya sipariş detayında ürün adını göstermek için
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {

    private Long id;

    // Entity'de "Product product" vardı → DTO'da sadece ID'sini tutuyoruz
    @NotNull(message = "Ürün ID boş olamaz")
    private Long productId;

    // Sipariş detayında ürün adını göstermek için (Service dolduracak)
    private String productName;

    @NotNull(message = "Adet boş olamaz")
    @Positive(message = "Adet en az 1 olmalı")
    private Integer quantity = 1;

    // Birim fiyat — kullanıcı göndermez, Service ürünün fiyatına bakıp set eder
    private BigDecimal unitPrice;
}
