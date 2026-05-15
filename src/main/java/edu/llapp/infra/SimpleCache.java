package edu.llapp.infra;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Simple in-memory cache with TTL expiry.
 * Used to reduce redundant API calls.
 */
public class SimpleCache<K, V> {
    private static final Logger logger = Logger.getLogger(SimpleCache.class.getName());

    private final Map<K, CacheEntry<V>> cache;
    private final long ttlMillis;

    public SimpleCache(int ttlMinutes) {
        this.cache = new ConcurrentHashMap<>();
        this.ttlMillis = ttlMinutes * 60 * 1000L;
    }

    /**
     * Store a value in the cache.
     */
    public void put(K key, V value) {
        long expiryTime = System.currentTimeMillis() + ttlMillis;
        cache.put(key, new CacheEntry<>(value, expiryTime));
        logger.fine("Cached: " + key);
    }

    /**
     * Retrieve a value from the cache. Returns null if missing or expired.
     */
    public V get(K key) {
        CacheEntry<V> entry = cache.get(key);

        if (entry == null) {
            logger.fine("Cache miss: " + key);
            return null;
        }

        if (entry.isExpired()) {
            cache.remove(key);
            logger.fine("Cache expired: " + key);
            return null;
        }

        logger.fine("Cache hit: " + key);
        return entry.value;
    }

    /**
     * Clear all cached entries.
     */
    public void clear() {
        cache.clear();
        logger.info("Cache cleared");
    }

    /**
     * Return the number of non-expired entries.
     */
    public int size() {
        cache.entrySet().removeIf(e -> e.getValue().isExpired());
        return cache.size();
    }

    /**
     * Internal cache entry holding a value and its expiry timestamp.
     */
    private static class CacheEntry<V> {
        final V value;
        final long expiryTime;

        CacheEntry(V value, long expiryTime) {
            this.value = value;
            this.expiryTime = expiryTime;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }
}
