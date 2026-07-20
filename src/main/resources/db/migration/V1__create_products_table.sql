-- ============================================================
-- V1__create_products_table.sql
-- ============================================================
-- Flyway Dosya İsimlendirme Kuralı:
--   V{versiyon}__{açıklama}.sql
--   ↑              ↑
--   |              +-- İKİ alt çizgi (__)  zorunlu!
--   +-- 'V' harfi ile başlar (Versioned migration)
--
-- Flyway bu dosyaları versiyon sırasına göre çalıştırır.
-- Bir kez çalıştırılan migration bir daha çalıştırılmaz
-- (flyway_schema_history tablosunda kayıt tutulur).
-- ============================================================

CREATE TABLE products (
    -- id: Primary key, otomatik artan (PostgreSQL BIGSERIAL = auto-increment BIGINT)
    id              BIGSERIAL       PRIMARY KEY,

    -- Ürün adı: boş olamaz, max 255 karakter
    name            VARCHAR(255)    NOT NULL,

    -- Ürün açıklaması: uzun metin için TEXT tipi (sınırsız uzunluk)
    description     TEXT,

    -- Fiyat: DECIMAL(10,2) = toplam 10 basamak, 2'si ondalık
    -- Örn: 99999999.99 (max)
    -- Para hesaplamalarında DECIMAL kullan, FLOAT/DOUBLE KULLANMA! (hassasiyet kaybı)
    price           DECIMAL(10, 2)  NOT NULL,

    -- Stok miktarı: varsayılan 0
    stock_quantity  INTEGER         NOT NULL DEFAULT 0,

    -- Ürün görseli URL'i (MinIO/S3'teki dosya yolu)
    -- NULL olabilir çünkü ürün resimsiz de eklenebilir
    image_url       VARCHAR(512),

    -- Zaman damgaları: ne zaman oluşturuldu / güncellendi
    -- TIMESTAMP WITH TIME ZONE: saat dilimi bilgisini de saklar (best practice)
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- Ürün adına index ekle → isme göre arama/sıralama hızlanır
CREATE INDEX idx_products_name ON products(name);
