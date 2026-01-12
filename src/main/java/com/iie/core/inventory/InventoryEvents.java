package com.iie.core.inventory;

public record InventoryEvents() {
    
    public record StockReserved(String sku, int quantity, String orderId) {}
    
    public record StockReleased(String sku, int quantity, String orderId) {}
    
    public record StockDepleted(String sku) {}
}
