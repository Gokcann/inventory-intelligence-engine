package com.iie.core.order;

public record OrderEvents() {
    
    public record OrderCreated(String orderId, String sku, int quantity) {}
    
    public record OrderCancelled(String orderId) {}
}
