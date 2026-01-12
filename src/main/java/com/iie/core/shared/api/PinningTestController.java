package com.iie.core.shared.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.locks.ReentrantLock;

@RestController
@RequestMapping("/api/test")
class PinningTestController {

    private final ReentrantLock lock = new ReentrantLock();
    private final Object syncLock = new Object();

    // GOOD: ReentrantLock - no pinning
    @GetMapping("/no-pinning")
    public String noPinning() throws InterruptedException {
        lock.lock();
        try {
            Thread.sleep(100);
            return "OK - ReentrantLock (no pinning)";
        } finally {
            lock.unlock();
        }
    }

    // BAD: synchronized - causes pinning!
    @GetMapping("/with-pinning")
    public String withPinning() throws InterruptedException {
        synchronized (syncLock) {
            Thread.sleep(100);
            return "OK - synchronized (PINNING!)";
        }
    }
}
