package com.baranproject.backendmastery.service;

import com.baranproject.backendmastery.dto.ProductDTO;
import com.baranproject.backendmastery.entity.Product;
import com.baranproject.backendmastery.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Ürün iş mantığı katmanı.
 *
 * @Slf4j  → log.info(), log.error() gibi loglama metodları otomatik gelir.
 * @Service → Spring bu sınıfı "iş katmanı bean'i" olarak tanır.
 * @RequiredArgsConstructor → final alanlar için constructor injection üretir.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    // ==================== İŞ METODLARI ====================

    public List<ProductDTO> getAllProducts() {
        log.info("Tüm ürünler listeleniyor");
        List<Product> products = productRepository.findAll();
        log.debug("Toplam {} ürün bulundu", products.size());
        return products.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ProductDTO getProductById(Long id) {
        log.info("Ürün aranıyor: id={}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Ürün bulunamadı: id={}", id);
                    return new RuntimeException("Ürün bulunamadı: " + id);
                });
        return toDTO(product);
    }

    @Transactional
    public ProductDTO createProduct(ProductDTO dto) {
        log.info("Yeni ürün oluşturuluyor: {}", dto.getName());
        Product product = toEntity(dto);
        Product saved = productRepository.save(product);
        log.info("Ürün başarıyla oluşturuldu: id={}, name={}", saved.getId(), saved.getName());
        return toDTO(saved);
    }

    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO dto) {
        log.info("Ürün güncelleniyor: id={}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ürün bulunamadı: " + id));

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStockQuantity(dto.getStockQuantity());
        if (dto.getImageUrl() != null) {
            product.setImageUrl(dto.getImageUrl());
        }

        Product updated = productRepository.save(product);
        log.info("Ürün güncellendi: id={}", updated.getId());
        return toDTO(updated);
    }

    @Transactional
    public void deleteProduct(Long id) {
        log.info("Ürün siliniyor: id={}", id);
        if (!productRepository.existsById(id)) {
            log.error("Silinecek ürün bulunamadı: id={}", id);
            throw new RuntimeException("Ürün bulunamadı: " + id);
        }
        productRepository.deleteById(id);
        log.info("Ürün silindi: id={}", id);
    }

    // Görsel URL güncelleme (MinIO'dan upload sonrası çağrılacak)
    @Transactional
    public void updateProductImage(Long id, String imageUrl) {
        log.info("Ürün görseli güncelleniyor: id={}, url={}", id, imageUrl);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ürün bulunamadı: " + id));
        product.setImageUrl(imageUrl);
        productRepository.save(product);
    }

    // ==================== DÖNÜŞÜM METODLARI ====================

    private ProductDTO toDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .imageUrl(product.getImageUrl())
                .build();
    }

    private Product toEntity(ProductDTO dto) {
        return Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .stockQuantity(dto.getStockQuantity())
                .imageUrl(dto.getImageUrl())
                .build();
    }
}
