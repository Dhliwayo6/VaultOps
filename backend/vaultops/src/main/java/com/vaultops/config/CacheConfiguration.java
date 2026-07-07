package com.vaultops.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Arrays;

@Configuration
@EnableCaching
@EnableScheduling
public class CacheConfiguration {

    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager manager = new ConcurrentMapCacheManager();
        manager.setAllowNullValues(false);
        // Register productCache and assetStatsCache.
        // Note: For a horizontally scaled production deployment, a distributed cache provider 
        // like Redis (e.g., via spring-boot-starter-data-redis) would be required instead of this local in-memory manager.
        manager.setCacheNames(Arrays.asList("productCache", "assetStatsCache"));
        return manager;
    }

    @CacheEvict(value = "productCache", allEntries = true)
    @Scheduled(fixedDelay = 10000, initialDelay = 0)
    public void evictDataCache() {
        System.out.println("Evicting data cache");
    }

    @CacheEvict(value = "assetStatsCache", allEntries = true)
    @Scheduled(fixedDelay = 60000, initialDelay = 60000)
    public void evictAssetStatsCache() {
        System.out.println("Evicting asset stats cache");
    }
}
