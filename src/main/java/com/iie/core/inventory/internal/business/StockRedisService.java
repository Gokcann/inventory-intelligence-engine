package com.iie.core.inventory.internal.business;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
class StockRedisService {

    private final StringRedisTemplate redis;
    private final RedisScript<Long> reserveScript;

    StockRedisService(StringRedisTemplate redis) {
        this.redis = redis;
        this.reserveScript = loadScript();
    }

    private RedisScript<Long> loadScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("scripts/stock_reservation.lua"));
        script.setResultType(Long.class);
        return script;
    }

    public boolean reserveStock(String sku, int quantity) {
        String key = "stock:" + sku;
        Long result = redis.execute(reserveScript, List.of(key), String.valueOf(quantity));
        return result != null && result >= 0;
    }

    public Optional<Integer> getStock(String sku) {
        String val = redis.opsForValue().get("stock:" + sku);
        return val != null ? Optional.of(Integer.parseInt(val)) : Optional.empty();
    }

    public void setStock(String sku, int quantity) {
        redis.opsForValue().set("stock:" + sku, String.valueOf(quantity));
    }
}
