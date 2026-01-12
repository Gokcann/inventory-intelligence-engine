package com.iie.core.inventory;

import java.util.Optional;

public interface InventoryManagement {
    
    boolean reserveStock(String sku, int quantity, String orderId);
    
    void releaseStock(String sku, int quantity, String orderId);
    
    Optional<Integer> getAvailableStock(String sku);
}
