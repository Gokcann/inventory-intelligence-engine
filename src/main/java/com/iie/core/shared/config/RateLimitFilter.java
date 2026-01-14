package com.iie.core.shared.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(1)
public class RateLimitFilter implements Filter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    // Default: 100 requests per minute per IP
    private static final int REQUESTS_PER_MINUTE = 100;
    
    // Auth endpoints: 10 requests per minute (stricter for login)
    private static final int AUTH_REQUESTS_PER_MINUTE = 10;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String path = httpRequest.getRequestURI();
        
        // Skip actuator and swagger
        if (path.startsWith("/actuator") || path.startsWith("/swagger") || path.startsWith("/api-docs")) {
            chain.doFilter(request, response);
            return;
        }
        
        String clientIp = getClientIp(httpRequest);
        String bucketKey = clientIp + ":" + (path.startsWith("/api/auth") ? "auth" : "api");
        
        Bucket bucket = buckets.computeIfAbsent(bucketKey, key -> createBucket(path));
        
        if (bucket.tryConsume(1)) {
            // Add rate limit headers
            httpResponse.setHeader("X-Rate-Limit-Remaining", String.valueOf(bucket.getAvailableTokens()));
            chain.doFilter(request, response);
        } else {
            httpResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write("""
                {"error": "Too many requests", "message": "Rate limit exceeded. Try again later."}
                """);
        }
    }

    private Bucket createBucket(String path) {
        int limit = path.startsWith("/api/auth") ? AUTH_REQUESTS_PER_MINUTE : REQUESTS_PER_MINUTE;
        
        Bandwidth bandwidth = Bandwidth.classic(limit, Refill.greedy(limit, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(bandwidth).build();
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
