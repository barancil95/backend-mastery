package com.baranproject.backendmastery.service;

import com.baranproject.backendmastery.config.RabbitMQConfig;
import com.baranproject.backendmastery.dto.OrderDTO;
import com.baranproject.backendmastery.dto.OrderItemDTO;
import com.baranproject.backendmastery.entity.Order;
import com.baranproject.backendmastery.entity.OrderItem;
import com.baranproject.backendmastery.entity.OrderStatus;
import com.baranproject.backendmastery.entity.Product;
import com.baranproject.backendmastery.event.OrderCreatedEvent;
import com.baranproject.backendmastery.repository.OrderRepository;
import com.baranproject.backendmastery.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final RabbitTemplate rabbitTemplate;

    // ==================== İŞ METODLARI ====================

    public List<OrderDTO> getAllOrders() {
        log.info("Tüm siparişler listeleniyor");
        return orderRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public OrderDTO getOrderById(Long id) {
        log.info("Sipariş aranıyor: id={}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Sipariş bulunamadı: id={}", id);
                    return new RuntimeException("Sipariş bulunamadı: " + id);
                });
        return toDTO(order);
    }

    /**
     * Yeni sipariş oluşturur.
     * 
     * İş mantığı:
     * 1. Her sipariş kalemi için ürünü DB'den bul
     * 2. Stok kontrolü yap
     * 3. Birim fiyatı ürünün güncel fiyatından al
     * 4. Stoktan düş
     * 5. Toplam tutarı hesapla
     * 6. Kaydet
     */
    @Transactional
    public OrderDTO createOrder(OrderDTO dto) {
        log.info("Yeni sipariş oluşturuluyor: müşteri={}", dto.getCustomerName());

        Order order = Order.builder()
                .customerName(dto.getCustomerName())
                .customerEmail(dto.getCustomerEmail())
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.ZERO)
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemDTO itemDTO : dto.getItems()) {
            // Ürünü bul
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Ürün bulunamadı: " + itemDTO.getProductId()));

            // Stok kontrolü
            if (product.getStockQuantity() < itemDTO.getQuantity()) {
                log.warn("Yetersiz stok: ürün={}, stok={}, istenen={}",
                        product.getName(), product.getStockQuantity(), itemDTO.getQuantity());
                throw new RuntimeException(
                        "Yetersiz stok: " + product.getName()
                        + " (Stok: " + product.getStockQuantity()
                        + ", İstenen: " + itemDTO.getQuantity() + ")");
            }

            // Stoktan düş
            product.setStockQuantity(product.getStockQuantity() - itemDTO.getQuantity());
            productRepository.save(product);

            // Sipariş kalemi oluştur
            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(itemDTO.getQuantity())
                    .unitPrice(product.getPrice())  // Fiyatı ürünün güncel fiyatından al
                    .build();

            // Çift yönlü ilişkiyi senkronize et
            order.addItem(orderItem);

            // Toplam tutara ekle (birim fiyat × adet)
            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity())));
        }

        order.setTotalAmount(totalAmount);
        Order saved = orderRepository.save(order);

        log.info("Sipariş oluşturuldu: id={}, toplam={}", saved.getId(), saved.getTotalAmount());

        // RabbitMQ'ya event fırlatılıyor
        try {
            OrderCreatedEvent event = OrderCreatedEvent.builder()
                    .orderId(saved.getId())
                    .customerName(saved.getCustomerName())
                    .customerEmail(saved.getCustomerEmail())
                    .totalAmount(saved.getTotalAmount())
                    .build();

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.ORDER_EXCHANGE,
                    RabbitMQConfig.ORDER_ROUTING_KEY,
                    event
            );
            log.info("Sipariş oluşturuldu eventi RabbitMQ'ya başarıyla gönderildi: id={}", saved.getId());
        } catch (Exception e) {
            // Event atılamasa bile siparişi geri almıyoruz (uygulama akışının bozulmaması için)
            // Gerçek projelerde bu gibi durumlar için Outbox Pattern veya retry mekanizmaları kurulur.
            log.error("RabbitMQ event gönderme hatası: {}", e.getMessage(), e);
        }

        return toDTO(saved);
    }

    // ==================== DÖNÜŞÜM METODLARI ====================

    private OrderDTO toDTO(Order order) {
        List<OrderItemDTO> itemDTOs = order.getItems().stream()
                .map(item -> OrderItemDTO.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .build())
                .collect(Collectors.toList());

        return OrderDTO.builder()
                .id(order.getId())
                .customerName(order.getCustomerName())
                .customerEmail(order.getCustomerEmail())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus().name())  // Enum → String
                .items(itemDTOs)
                .build();
    }
}
