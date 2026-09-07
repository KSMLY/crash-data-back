package com.crashdata.back.config;

import com.github.benmanes.caffeine.cache.Cache;
import org.junit.jupiter.api.Test;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.caffeine.CaffeineCacheManager;

import java.time.Duration;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * The manager is built by hand rather than through a Spring context, so the
 * injected settings are asserted directly: nothing an endpoint test can see
 * changes when expireAfterWrite or maximumSize is dropped on the floor.
 */
class CacheConfigTest {

    private static final Duration EXPIRE_AFTER_WRITE = Duration.ofMinutes(30);
    private static final long MAXIMUM_SIZE = 500L;

    private final CaffeineCacheManager manager =
            new CacheConfig().cacheManager(EXPIRE_AFTER_WRITE, MAXIMUM_SIZE);

    @Test
    void managesTheThreeLocationCaches() {
        assertEquals(Set.of("governorates", "districts", "municipalities"),
                Set.copyOf(manager.getCacheNames()));
    }

    @Test
    void appliesTheInjectedExpiry() {
        assertEquals(EXPIRE_AFTER_WRITE,
                nativeCache("governorates").policy().expireAfterWrite().orElseThrow().getExpiresAfter());
    }

    @Test
    void appliesTheInjectedMaximumSize() {
        assertEquals(MAXIMUM_SIZE,
                nativeCache("governorates").policy().eviction().orElseThrow().getMaximum());
    }

    private Cache<Object, Object> nativeCache(String name) {
        CaffeineCache cache = (CaffeineCache) manager.getCache(name);
        assertNotNull(cache, name);
        return cache.getNativeCache();
    }
}
