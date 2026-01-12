package com.iie.core.shared;

import java.time.Instant;
import java.util.UUID;

public final class IdGenerator {
    
    private IdGenerator() {}
    
    public static String generate() {
        return UUID.randomUUID().toString();
    }
    
    public static String timestamped() {
        return Instant.now().toEpochMilli() + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
}
