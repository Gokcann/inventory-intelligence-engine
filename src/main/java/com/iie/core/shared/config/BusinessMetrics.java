package com.iie.core.shared.config;

import com.iie.core.shared.events.OrderCreated;
import com.iie.core.shared.events.StockReserved;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class BusinessMetrics {

    private final Counter ordersCreated;
    private final Counter stockReservations;

    public BusinessMetrics(MeterRegistry registry) {
        this.ordersCreated = Counter.builder("iie.orders.created")
            .description("Total orders created")
            .register(registry);

        this.stockReservations = Counter.builder("iie.stock.reservations")
            .description("Total stock reservations")
            .register(registry);
    }

    @EventListener
    public void onOrderCreated(OrderCreated event) {
        ordersCreated.increment();
    }

    @EventListener
    public void onStockReserved(StockReserved event) {
        stockReservations.increment();
    }
}
