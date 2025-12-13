package edu.llapp.infra;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple memory caching
 * Used to reduce the number of API calls
 */
public class SimpleCache<K, V> {
    private final Map<K, CacheEntry<V>> cache;
    private final long ttlMillis;

    public SimpleCache(int ttlMinutes) {
        this.cache = new ConcurrentHashMap<>();
        this.ttlMillis = ttlMinutes * 60 * 1000L;
    }

    /**
     * Deposit into Cache
     */
    public void put(K key, V value) {
        long expiryTime = System.currentTimeMillis() + ttlMillis;
        cache.put(key, new CacheEntry<>(value, expiryTime));
        System.out.println("Cached: " + key);
    }

    /**
     * Get it from cache (if hasn't expired).
     */
    public V get(K key) {
        CacheEntry<V> entry = cache.get(key);

        if (entry == null) {
            System.out.println("Cache miss: " + key);
            return null;
        }

        if (entry.isExpired()) {
            cache.remove(key);
            System.out.println("Cache expired: " + key);
            return null;
        }

        System.out.println("Cache hit: " + key);
        return entry.value;
    }

    /**
     * Empty cache
     */
    public void clear() {
        cache.clear();
        System.out.println("🗑️ Cache cleared");
    }

    /**
     * Get cache size
     */
    public int size() {
        // Remove expired items
        cache.entrySet().removeIf(e -> e.getValue().isExpired());
        return cache.size();
    }

    /**
     * Cache Project
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