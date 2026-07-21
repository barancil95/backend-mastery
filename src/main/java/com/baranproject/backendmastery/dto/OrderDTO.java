package com.baranproject.backendmastery.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Sipariş DTO'su.
 *
 * Entity'deki Order'dan farkları:
 *   - "createdAt", "updatedAt" yok → sistem yönetir
 *   - "status" String olarak tutuldu → JSON'da enum yerine string daha temiz
 *   - "items" listesi OrderItemDTO tipinde → Entity değil, DTO taşıyor
 *   - @Email ile e-posta formatı doğrulanıyor
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    private Long id;

    @NotBlank(message = "Müşteri adı boş olamaz")
    private String customerName;

    @NotBlank(message = "E-posta boş olamaz")
    @Email(message = "Geçerli bir e-posta adresi girin")
    private String customerEmail;

    // Service hesaplayacak (ürün fiyatı × adet toplamı)
    private BigDecimal totalAmount;

    // String olarak tutuyoruz — "PENDING", "CONFIRMED" vs.
    private String status;

    // Sipariş kalemleri — en az 1 kalem olmalı (boş sipariş olmaz)
    @NotEmpty(message = "Sipariş en az 1 ürün içermeli")
    @Valid
    private List<OrderItemDTO> items;
}
