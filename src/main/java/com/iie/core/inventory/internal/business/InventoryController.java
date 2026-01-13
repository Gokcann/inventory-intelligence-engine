package com.iie.core.inventory.internal.business;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final StockRedisService stockService;

    public InventoryController(StockRedisService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("/{sku}")
    public ResponseEntity<?> getStock(@PathVariable String sku) {
        return stockService.getStock(sku)
            .map(qty -> ResponseEntity.ok(Map.of(
                "sku", sku,
                "available", qty,
                "reserved", 0
            )))
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{sku}")
    public ResponseEntity<?> setStock(@PathVariable String sku, @RequestBody Map<String, Integer> request) {
        int quantity = request.getOrDefault("available", 0);
        stockService.setStock(sku, quantity);
        return ResponseEntity.ok(Map.of(
            "sku", sku,
            "available", quantity,
            "reserved", 0
        ));
    }

    @PostMapping("/{sku}/reserve")
    public ResponseEntity<?> reserveStock(@PathVariable String sku, @RequestBody Map<String, Object> request) {
        int quantity = (Integer) request.getOrDefault("quantity", 0);
        boolean success = stockService.reserveStock(sku, quantity);
        
        if (success) {
            int remaining = stockService.getStock(sku).orElse(0);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "remainingStock", remaining,
                "message", "Reserved"
            ));
        }
        
        return ResponseEntity.status(409).body(Map.of(
            "success", false,
            "message", "Insufficient stock"
        ));
    }
}
