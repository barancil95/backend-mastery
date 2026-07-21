package com.baranproject.backendmastery.listener;

import com.baranproject.backendmastery.config.RabbitMQConfig;
import com.baranproject.backendmastery.entity.OrderStatus;
import com.baranproject.backendmastery.event.OrderCreatedEvent;
import com.baranproject.backendmastery.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ Kuyruğunu Dinleyen Consumer (Listener) Sınıfı
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final OrderRepository orderRepository;

    /**
     * order.queue kuyruğuna gelen mesajları asenkron olarak dinler.
     * 
     * @RabbitListener: Spring AMQP'ye bu metodun belirtilen kuyruğu dinleyeceğini söyler.
     * Jackson2JsonMessageConverter kullandığımız için gelen JSON verisi otomatik 
     * OrderCreatedEvent objesine deserilize edilir.
     */
    @RabbitListener(queues = RabbitMQConfig.ORDER_QUEUE)
    public void handleOrderCreatedEvent(OrderCreatedEvent event) {
        log.info(">>>> [ASENKRON ALICI] Yeni sipariş eventi yakalandı! <<<<");
        log.info("Sipariş ID: {}", event.getOrderId());
        log.info("Müşteri: {}", event.getCustomerName());
        log.info("E-posta: {}", event.getCustomerEmail());
        log.info("Toplam Tutar: ₺{}", event.getTotalAmount());
        
        // Asenkron olarak veritabanına gidip siparişi CONFIRMED (Onaylandı) yapıyoruz
        orderRepository.findById(event.getOrderId()).ifPresent(order -> {
            order.setStatus(OrderStatus.CONFIRMED);
            orderRepository.save(order);
            log.info(">>>> [ASENKRON ALICI] Sipariş durumu CONFIRMED olarak güncellendi: ID={} <<<<", order.getId());
        });

        log.info(">>>> [ASENKRON ALICI] Sipariş işleme süreci başarıyla tamamlandı. <<<<");
    }
}
