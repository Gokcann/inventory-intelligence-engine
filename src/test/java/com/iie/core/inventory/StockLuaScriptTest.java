package com.iie.core.inventory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class StockLuaScriptTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
        registry.add("spring.docker.compose.enabled", () -> false);
    }

    @Autowired
    StringRedisTemplate redisTemplate;

    DefaultRedisScript<Long> script;

    @BeforeEach
    void setUp() {
        script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("scripts/stock_reservation.lua"));
        script.setResultType(Long.class);
    }

    @Test
    void shouldDecreaseStockWhenSufficient() {
        // given: stock:SKU1 = 5
        redisTemplate.opsForValue().set("stock:SKU1", "5");

        // when: reserve 3
        Long result = redisTemplate.execute(script, List.of("stock:SKU1"), "3");

        // then: returns new balance 2
        assertThat(result).isEqualTo(2);
        assertThat(redisTemplate.opsForValue().get("stock:SKU1")).isEqualTo("2");
    }

    @Test
    void shouldFailWhenInsufficientStock() {
        // given: stock:SKU2 = 5
        redisTemplate.opsForValue().set("stock:SKU2", "5");

        // when: try to reserve 6
        Long result = redisTemplate.execute(script, List.of("stock:SKU2"), "6");

        // then: returns -1 (failure), stock unchanged
        assertThat(result).isEqualTo(-1);
        assertThat(redisTemplate.opsForValue().get("stock:SKU2")).isEqualTo("5");
    }

    @Test
    void shouldHandleExactStock() {
        // given: stock:SKU3 = 5
        redisTemplate.opsForValue().set("stock:SKU3", "5");

        // when: reserve exactly 5
        Long result = redisTemplate.execute(script, List.of("stock:SKU3"), "5");

        // then: returns 0 (success, zero remaining)
        assertThat(result).isEqualTo(0);
        assertThat(redisTemplate.opsForValue().get("stock:SKU3")).isEqualTo("0");
    }
}
