package com.baranproject.backendmastery.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Sipariş oluşturulduğunda RabbitMQ kuyruğuna fırlatacağımız Event nesnesi.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent implements Serializable {

    private Long orderId;
    private String customerName;
    private String customerEmail;
    private BigDecimal totalAmount;
}
