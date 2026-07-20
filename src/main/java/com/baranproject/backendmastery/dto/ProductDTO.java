package com.baranproject.backendmastery.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Long id;

    @NotBlank(message = "Ürün adı boş olamaz")
    private String name;

    private String description;

    @NotNull(message = "Fiyat boş olamaz")
    @Positive(message = "Fiyat sıfırdan büyük olmalı")
    private BigDecimal price;

    @NotNull(message = "Stok miktarı boş olamaz")
    @PositiveOrZero(message = "Stok miktarı negatif olamaz")
    private Integer stockQuantity;

    private String imageUrl;
}
