-- ============================================================
-- V2__create_orders_table.sql
-- ============================================================
-- Sipariş sistemi iki tablodan oluşur:
--   1. orders       → Siparişin kendisi (müşteri bilgisi, toplam tutar, durum)
--   2. order_items  → Siparişe ait ürünler (hangi ürün, kaç adet, birim fiyat)
--
-- Bu ilişki "One-to-Many" ilişkisidir:
--   Bir sipariş (order) → birden fazla sipariş kalemi (order_item) içerebilir.
-- ============================================================

-- Sipariş durumlarını tutan ENUM tipi
-- PostgreSQL'de özel bir tip olarak tanımlanır (Java enum ile eşleşecek)
CREATE TYPE order_status AS ENUM ('PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED');

CREATE TABLE orders (
    id              BIGSERIAL           PRIMARY KEY,

    -- Müşteri bilgileri (basit tutuyoruz, gerçek projede ayrı tablo olur)
    customer_name   VARCHAR(255)        NOT NULL,
    customer_email  VARCHAR(255)        NOT NULL,

    -- Sipariş toplam tutarı
    total_amount    DECIMAL(12, 2)      NOT NULL DEFAULT 0,

    -- Sipariş durumu: ENUM tipi kullanıyoruz
    -- VARCHAR da kullanılabilirdi ama ENUM ile sadece geçerli değerler kabul edilir
    status          order_status        NOT NULL DEFAULT 'PENDING',

    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- ============================================================
-- Sipariş Kalemleri (Order Items)
-- ============================================================
-- Her satır, bir siparişe ait bir ürünü temsil eder.
-- Foreign Key ile hem orders hem products tablosuna bağlıdır.

CREATE TABLE order_items (
    id              BIGSERIAL           PRIMARY KEY,

    -- Hangi siparişe ait? → orders tablosuna FK
    -- ON DELETE CASCADE: Sipariş silinirse, kalemleri de silinir
    order_id        BIGINT              NOT NULL
                    REFERENCES orders(id) ON DELETE CASCADE,

    -- Hangi ürün sipariş edildi? → products tablosuna FK
    -- ON DELETE RESTRICT: Siparişi olan ürün silinemez (veri bütünlüğü)
    product_id      BIGINT              NOT NULL
                    REFERENCES products(id) ON DELETE RESTRICT,

    -- Kaç adet sipariş edildi
    quantity        INTEGER             NOT NULL,

    -- Sipariş anındaki birim fiyat
    -- (Ürün fiyatı sonradan değişse bile, siparişteki fiyat korunur)
    unit_price      DECIMAL(10, 2)      NOT NULL,

    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- Bir siparişin kalemlerine hızlı erişim için index
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
-- Bir ürünün hangi siparişlerde olduğunu bulmak için index
CREATE INDEX idx_order_items_product_id ON order_items(product_id);
-- Müşteri e-postasına göre sipariş arama için index
CREATE INDEX idx_orders_customer_email ON orders(customer_email);
