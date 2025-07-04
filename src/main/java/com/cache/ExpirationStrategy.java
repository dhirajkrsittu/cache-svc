package com.cache;

// Multiple strategies can be created by implementing this interface
public interface ExpirationStrategy<K, V> {
    void removeExpiredEntries(Cache<K, V> cache);
    boolean isExpired(Cache<K, V> cache, CacheEntry<K, V> entry);
}
