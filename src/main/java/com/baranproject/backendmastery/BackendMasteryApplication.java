package com.baranproject.backendmastery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Uygulamanın giriş noktası (Entry Point).
 *
 * @SpringBootApplication anotasyonu 3 şeyi bir arada yapar:
 *
 * 1. @Configuration     → Bu sınıf bir Spring konfigürasyon kaynağıdır.
 * 2. @EnableAutoConfiguration → Spring Boot, classpath'teki kütüphanelere bakarak
 *                               otomatik konfigürasyon yapar (örn: PostgreSQL driver
 *                               varsa DataSource otomatik oluşturulur).
 * 3. @ComponentScan     → Bu paketin (com.baranproject.backendmastery) ve tüm alt
 *                         paketlerinin taranarak @Service, @Repository, @Controller
 *                         gibi bileşenlerin Spring Container'a eklenmesini sağlar.
 */
@SpringBootApplication
public class BackendMasteryApplication {

    public static void main(String[] args) {
        // SpringApplication.run() → Gömülü Tomcat'i başlatır,
        // tüm bean'leri oluşturur ve uygulamayı ayağa kaldırır.
        SpringApplication.run(BackendMasteryApplication.class, args);
    }
}
