# 🛒 MasteryMarket — Distributed E-Commerce Marketplace & Cloud Architecture

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)](https://www.postgresql.org/)
[![RabbitMQ](https://img.shields.io/badge/RabbitMQ-3.12-red.svg)](https://www.rabbitmq.com/)
[![AWS S3](https://img.shields.io/badge/AWS-S3%20Bucket-yellow.svg)](https://aws.amazon.com/s3/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED.svg)](https://www.docker.com/)
[![CI/CD](https://img.shields.io/badge/GitHub-Actions-2088FF.svg)](https://github.com/features/actions)

**MasteryMarket**, olay odaklı mimari (Event-Driven Architecture), bulut depolama, rol bazlı güvenlik (RBAC) ve otomatik CI/CD süreçlerini içeren dağıtık bir e-ticaret pazaryeri altyapısıdır.

---

## 📌 Mimari & Öne Çıkan Özellikler

### 🛡️ 1. Rol Bazlı Güvenlik & Kimlik Doğrulama (Spring Security 6)
* **RBAC Yapısı:** `ROLE_ADMIN` ve `ROLE_USER` yetkilendirmesi.
* **Şifreleme:** Kullanıcı şifreleri **BCrypt** algoritması ile hash'lenerek veritabanında saklanır.
* **Yetki Kuralları:** Ürün ekleme/düzenleme/silme işlemleri **Sadece ADMIN** rolüne açıktır. Sipariş verme işlemleri giriş yapmış tüm kullanıcılara açıktır.
* **Otomatik Seeder:** Uygulama başlangıcında `DataSeeder` ile varsayılan hesaplar otomatik oluşturulur.

### 🐇 2. Olay Odaklı Mesajlaşma (Event-Driven Architecture - RabbitMQ)
* Sipariş oluşturulduğunda ana uygulama kilitlenmez (Non-blocking).
* Sipariş bilgileri **RabbitMQ (`order.queue`)** kuyruğuna asenkron mesaj olarak fırlatılır.
* `OrderEventListener` kuyruktan mesajı yakalayarak sipariş durumunu otomatik `CONFIRMED` olarak günceller (Ödeme/Kargo simülasyonu).

### ☁️ 3. AWS S3 Bulut Medya Yönetimi
* Yüklenen ürün görselleri yerel diske değil, gerçek **AWS S3 Bucket (`eu-north-1` Stockholm)** üzerine yüklenir.
* MinIO uyumlu SDK ile bulut depolama yönetimi sağlanır.

### 🚀 4. Otomatik CI/CD & Bulut Dağıtımı (GitHub Actions + AWS EC2 + Docker)
* **Sürekli Entegrasyon (CI):** Her `git push` işleminde GitHub Actions üzerinde kod derlenir (`-DskipTests`).
* **Sürekli Dağıtım (CD):** Derlenen kod SSH protokolü ile **AWS EC2** canlı sunucusuna aktarılır.
* **Konteynerizasyon:** `postgres-db`, `rabbitmq`, `minio` ve `spring-app` servisleri izole Docker konteynerleri olarak çalışır.
* **Sanal Bellek & Temizlik:** Zayıf EC2 sunucusunun kilitlenmemesi için 2 GB Swap alanı yapılandırılmış ve `docker system prune` otomatik temizliği entegre edilmiştir.

---

## 🧱 Sistem Mimarisi

```text
               +-------------------------------------------------+
               |              Kullanıcı / Tarayıcı                |
               +-----------------------+-------------------------+
                                       |
                                HTTP / HTTPS (Port 8080)
                                       v
               +-------------------------------------------------+
               |           Spring Boot 3 (Spring Security)       |
               +----------+------------+------------+------------+
                          |            |            |
             SQL Queries  |   Events   |  Uploads   |
                          v            v            v
               +----------+----+  +----+----+  +----+----+
               | PostgreSQL 16 |  | RabbitMQ|  | AWS S3  |
               |  Veritabanı   |  | Kuyruk  |  | Bulut   |
               +---------------+  +---------+  +---------+
```

---

## 🛠️ Teknoloji Yığını (Tech Stack)

* **Backend:** Java 17, Spring Boot 3.3.5, Spring Data JPA, Spring Security 6
* **Database & Migration:** PostgreSQL 16, Flyway Migration
* **Messaging:** RabbitMQ (Spring AMQP)
* **Cloud Storage:** AWS S3 (Amazon Web Services)
* **Frontend Template:** Thymeleaf, Bootstrap 5, Thymeleaf Security Extras
* **DevOps & Cloud:** Docker, Docker Compose, AWS EC2, GitHub Actions CI/CD

---

## 🔑 Varsayılan Giriş Bilgileri (DataSeeder)

| Rol | E-Posta | Şifre | Yetkiler |
| :--- | :--- | :--- | :--- |
| **Yönetici (Admin)** | `admin@masterymarket.com` | `admin123` | Tüm Yetkiler (Ürün Ekle/Sil, Tüm Siparişleri Gör) |
| **Müşteri (User)** | `user@masterymarket.com` | `user123` | Sipariş Ver, Kendi Siparişlerini Gör |

---

## 💼 CV / Özgeçmiş Maddesi (Örnek Kullanım)

> **MasteryMarket — Distributed E-Commerce Marketplace & Cloud Architecture**
> * **Event-Driven Architecture:** Sipariş süreçlerinde sistem bileşenlerini birbirinden ayırmak (decoupling) ve asenkron işlemek için **RabbitMQ** mesaj kuyruğu mimarisi kurgulandı.
> * **Bulut Altyapısı & S3:** Görsel depolama yönetimi için **AWS S3 Bucket (Stockholm `eu-north-1`)** entegrasyonu sağlandı.
> * **Otomatik CI/CD:** GitHub Actions kullanılarak her `push` işleminde **AWS EC2** sunucusuna otomatik derleme, sıfır kesintili (zero-downtime) deploy ve Docker boru hattı kuruldu.
> * **Spring Security:** BCrypt şifreleme, RBAC (Rol bazlı yetkilendirme) ve custom `UserDetailsService` ile kimlik doğrulama mimarisi oluşturuldu.
