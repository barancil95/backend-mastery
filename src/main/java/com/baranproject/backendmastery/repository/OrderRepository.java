package com.baranproject.backendmastery.repository;

import com.baranproject.backendmastery.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Order Repository — Sipariş veritabanı erişim katmanı.
 *
 * Spring Data JPA, metod adından otomatik SQL üretir (Query Derivation).
 * Aşağıdaki metod adını parse ederek şunu üretir:
 *   findByCustomerEmail → SELECT * FROM orders WHERE customer_email = ?
 *
 * İsimlendirme kuralı:
 *   findBy{AlanAdı} → WHERE koşulu oluşturur
 *   OrderBy{AlanAdı}Desc → ORDER BY ... DESC ekler
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Müşteri e-postasına göre siparişleri bulur, en yeniden eskiye sıralar.
     *
     * Spring Data JPA bu metod adını parse eder ve şu SQL'i üretir:
     *   SELECT * FROM orders
     *   WHERE customer_email = :customerEmail
     *   ORDER BY created_at DESC
     *
     * Hiç SQL yazmadık — sadece metod adı ile sorguyu tanımladık!
     */
    List<Order> findByCustomerEmailOrderByCreatedAtDesc(String customerEmail);
}
