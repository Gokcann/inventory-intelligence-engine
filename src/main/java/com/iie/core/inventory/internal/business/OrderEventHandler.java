package com.iie.core.inventory.internal.business;

import com.iie.core.order.OrderEvents.OrderCreated;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
class OrderEventHandler {

    private final StockRedisService stockService;

    OrderEventHandler(StockRedisService stockService) {
        this.stockService = stockService;
    }

    @Async
    @EventListener
    void handleOrderCreated(OrderCreated event) {
        boolean reserved = stockService.reserveStock(event.sku(), event.quantity());
        if (!reserved) {
            // TODO: publish StockReservationFailed event
        }
    }
}
