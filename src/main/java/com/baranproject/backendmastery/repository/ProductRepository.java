package com.baranproject.backendmastery.repository;

import com.baranproject.backendmastery.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Product Repository — Veritabanı erişim katmanı (Data Access Layer).
 *
 * === Spring Data JPA'nın Büyüsü ===
 *
 * JpaRepository<Product, Long> interface'ini extend etmek yeterli!
 * Spring, runtime'da bu interface'in implementasyonunu OTOMATİK üretir.
 *
 * Parametreler:
 *   Product → Hangi Entity üzerinde çalışacak
 *   Long    → Primary Key'in tipi
 *
 * Hiç kod yazmadan şu metodlara sahip olursun:
 *   - findAll()         → Tüm ürünleri listele
 *   - findById(id)      → ID ile ürün bul (Optional<Product> döner)
 *   - save(product)     → Ürün ekle veya güncelle
 *   - deleteById(id)    → ID ile ürün sil
 *   - count()           → Toplam ürün sayısı
 *   - existsById(id)    → ID'li ürün var mı?
 *   ... ve daha fazlası!
 *
 * @Repository → Spring'e bu interface'in bir veri erişim bileşeni olduğunu söyler.
 *               Aslında JpaRepository extend ettiğinde otomatik tanınır ama
 *               açıkça yazmak kodun okunabilirliğini artırır (best practice).
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Şu an ekstra metod tanımlamaya gerek yok.
    // İleride ihtiyaç oldukça buraya query method'lar ekleyeceğiz.
    // Örnek: List<Product> findByNameContainingIgnoreCase(String keyword);
}
