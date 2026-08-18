package com.crashdata.back.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import java.time.Duration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CaffeineCacheManager cacheManager(@Value("${app.cache.expire-after-write}") Duration expireAfterWrite,
                                             @Value("${app.cache.maximum-size}") long maximumSize) {

        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                "governorates", "districts", "municipalities");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(expireAfterWrite)
                .maximumSize(maximumSize));
        return cacheManager;
    }
}
