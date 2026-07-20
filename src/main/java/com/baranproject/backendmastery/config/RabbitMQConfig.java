package com.baranproject.backendmastery.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ Konfigürasyon Sınıfı
 */
@Configuration
public class RabbitMQConfig {

    // Kuyruk (Queue), Exchange ve Routing Key isimleri
    public static final String ORDER_QUEUE = "order.queue";
    public static final String ORDER_EXCHANGE = "order.exchange";
    public static final String ORDER_ROUTING_KEY = "order.routingKey";

    /**
     * Mesajların birikeceği kuyruğu tanımlar.
     */
    @Bean
    public Queue orderQueue() {
        // durable: true -> RabbitMQ çökerse veya yeniden başlarsa kuyruktaki mesajlar kaybolmaz.
        return new Queue(ORDER_QUEUE, true);
    }

    /**
     * Mesajları kuyruklara yönlendirecek olan Exchange'i tanımlar.
     * DirectExchange: Gelen mesajı birebir routingKey eşleşmesine göre yönlendirir.
     */
    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(ORDER_EXCHANGE);
    }

    /**
     * Kuyruk ile Exchange arasındaki bağlantıyı (Binding) sağlar.
     * Exchange'e ORDER_ROUTING_KEY ile gelen mesajlar ORDER_QUEUE'ya iletilir.
     */
    @Bean
    public Binding orderBinding(Queue orderQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(orderQueue).to(orderExchange).with(ORDER_ROUTING_KEY);
    }

    /**
     * RabbitMQ'ya gönderilen Java objelerinin otomatik JSON'a çevrilmesini sağlar.
     * Varsayılan olarak Java Serializer kullanılır (binary), fakat JSON okumak ve 
     * farklı servislerin de (örneğin Node.js veya Python servisleri) bu mesajları okuyabilmesi 
     * için JSON formatı endüstri standardıdır.
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
