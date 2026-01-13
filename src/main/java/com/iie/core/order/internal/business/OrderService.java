package com.iie.core.order.internal.business;

import com.iie.core.inventory.InventoryManagement;
import com.iie.core.shared.events.OrderCreated;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
class OrderService {

    private final InventoryManagement inventory;
    private final ApplicationEventPublisher events;

    OrderService(InventoryManagement inventory, ApplicationEventPublisher events) {
        this.inventory = inventory;
        this.events = events;
    }

    public String createOrder(String sku, int quantity) {
        String orderId = java.util.UUID.randomUUID().toString();
        
        boolean reserved = inventory.reserveStock(sku, quantity, orderId);
        if (!reserved) {
            throw new IllegalStateException("Insufficient stock for SKU: " + sku);
        }
        
        events.publishEvent(new OrderCreated(orderId, sku, quantity));
        return orderId;
    }
}
