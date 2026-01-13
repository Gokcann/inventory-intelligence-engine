package com.iie.core.shared.events;

public record OrderCreated(String orderId, String sku, int quantity) {}
