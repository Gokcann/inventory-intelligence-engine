package com.iie.core.inventory.internal.business;

import com.iie.core.inventory.InventoryManagement;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
class InventoryService implements InventoryManagement {

    private final StockRedisService redisService;

    InventoryService(StockRedisService redisService) {
        this.redisService = redisService;
    }

    @Override
    public boolean reserveStock(String sku, int quantity, String orderId) {
        return redisService.reserveStock(sku, quantity);
    }

    @Override
    public void releaseStock(String sku, int quantity, String orderId) {
        redisService.setStock(sku, redisService.getStock(sku).orElse(0) + quantity);
    }

    @Override
    public Optional<Integer> getAvailableStock(String sku) {
        return redisService.getStock(sku);
    }
}
