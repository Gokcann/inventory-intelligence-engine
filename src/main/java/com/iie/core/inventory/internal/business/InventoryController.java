package com.iie.core.inventory.internal.business;

import com.iie.core.inventory.api.InventoryApi;
import com.iie.core.inventory.api.model.ReserveRequest;
import com.iie.core.inventory.api.model.ReserveResponse;
import com.iie.core.inventory.api.model.StockResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
class InventoryController implements InventoryApi {

    private final StockRedisService stockService;

    InventoryController(StockRedisService stockService) {
        this.stockService = stockService;
    }

    @Override
    public ResponseEntity<StockResponse> getStock(String sku) {
        return stockService.getStock(sku)
            .map(qty -> ResponseEntity.ok(new StockResponse().sku(sku).available(qty).reserved(0)))
            .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<ReserveResponse> reserveStock(String sku, ReserveRequest request) {
        boolean success = stockService.reserveStock(sku, request.getQuantity());
        
        if (success) {
            int remaining = stockService.getStock(sku).orElse(0);
            return ResponseEntity.ok(new ReserveResponse()
                .success(true)
                .remainingStock(remaining)
                .message("Reserved"));
        }
        
        return ResponseEntity.status(409).body(new ReserveResponse()
            .success(false)
            .message("Insufficient stock"));
    }
}
