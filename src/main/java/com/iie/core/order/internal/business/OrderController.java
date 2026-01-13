package com.iie.core.order.internal.business;

import com.iie.core.inventory.InventoryManagement;
import com.iie.core.shared.events.OrderCreated;
import com.iie.core.order.api.OrdersApi;
import com.iie.core.order.api.model.CreateOrderRequest;
import com.iie.core.order.api.model.OrderResponse;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
class OrderController implements OrdersApi {

    private final InventoryManagement inventory;
    private final ApplicationEventPublisher events;
    private final Map<String, OrderResponse> orders = new ConcurrentHashMap<>();

    OrderController(InventoryManagement inventory, ApplicationEventPublisher events) {
        this.inventory = inventory;
        this.events = events;
    }

    @Override
    public ResponseEntity<OrderResponse> createOrder(CreateOrderRequest request) {
        String orderId = UUID.randomUUID().toString();
        
        boolean reserved = inventory.reserveStock(request.getSku(), request.getQuantity(), orderId);
        if (!reserved) {
            return ResponseEntity.status(409).body(new OrderResponse()
                .orderId(orderId)
                .sku(request.getSku())
                .quantity(request.getQuantity())
                .status(OrderResponse.StatusEnum.CANCELLED));
        }

        events.publishEvent(new OrderCreated(orderId, request.getSku(), request.getQuantity()));

        OrderResponse response = new OrderResponse()
            .orderId(orderId)
            .sku(request.getSku())
            .quantity(request.getQuantity())
            .status(OrderResponse.StatusEnum.CONFIRMED)
            .createdAt(OffsetDateTime.now());

        orders.put(orderId, response);
        return ResponseEntity.status(201).body(response);
    }

    @Override
    public ResponseEntity<OrderResponse> getOrder(String orderId) {
        OrderResponse order = orders.get(orderId);
        return order != null 
            ? ResponseEntity.ok(order) 
            : ResponseEntity.notFound().build();
    }
}
