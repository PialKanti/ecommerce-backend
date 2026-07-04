package com.example.ecommerce.backend.common.config;

import com.github.benmanes.caffeine.cache.Caffeine;

import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CacheConfig {

    public static final String CACHE_PRODUCTS = "products";
    public static final String CACHE_CATEGORIES = "categories";
    public static final String CACHE_CATEGORIES_LIST = "categories-list";

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.registerCustomCache(
                CACHE_PRODUCTS,
                Caffeine.newBuilder().expireAfterWrite(Duration.ofMinutes(30)).build()
        );
        cacheManager.registerCustomCache(
                CACHE_CATEGORIES,
                Caffeine.newBuilder().expireAfterWrite(Duration.ofHours(1)).build()
        );
        cacheManager.registerCustomCache(
                CACHE_CATEGORIES_LIST,
                Caffeine.newBuilder().expireAfterWrite(Duration.ofMinutes(10)).build()
        );
        return cacheManager;
    }
}
