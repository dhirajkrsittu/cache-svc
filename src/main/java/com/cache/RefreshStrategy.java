package com.cache;

public interface RefreshStrategy<K, V> {
    void refresh(CacheEntry<K, V> cacheEntry);
}
