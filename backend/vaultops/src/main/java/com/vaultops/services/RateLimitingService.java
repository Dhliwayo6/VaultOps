package com.vaultops.services;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitingService {

    // Store general rate limiting buckets
    private final Map<String, Bucket> generalBuckets = new ConcurrentHashMap<>();

    // Store failed login lockout buckets
    private final Map<String, Bucket> failedLoginBuckets = new ConcurrentHashMap<>();

    public Bucket resolveGeneralBucket(String key, int capacity, Duration duration) {
        return generalBuckets.computeIfAbsent(key, k -> {
            Refill refill = Refill.intervally(capacity, duration);
            Bandwidth limit = Bandwidth.classic(capacity, refill);
            return Bucket.builder().addLimit(limit).build();
        });
    }

    public Bucket resolveFailedLoginBucket(String key) {
        // lockout limit: 5 failed attempts per 15 minutes
        return failedLoginBuckets.computeIfAbsent(key, k -> {
            Refill refill = Refill.intervally(5, Duration.ofMinutes(15));
            Bandwidth limit = Bandwidth.classic(5, refill);
            return Bucket.builder().addLimit(limit).build();
        });
    }

    public void resetFailedLoginBucket(String key) {
        failedLoginBuckets.remove(key);
    }

    public void clearAllBuckets() {
        generalBuckets.clear();
        failedLoginBuckets.clear();
    }
}
