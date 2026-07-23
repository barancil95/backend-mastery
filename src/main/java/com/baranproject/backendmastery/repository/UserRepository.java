package com.baranproject.backendmastery.repository;

import com.baranproject.backendmastery.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // E-posta adresine göre kullanıcı arar (Giriş yaparken Spring Security
    // kullanacak)
    Optional<User> findByEmail(String email);

    // E-posta adresinin sistemde zaten kayıtlı olup olmadığını kontrol eder
    boolean existsByEmail(String email);
}
