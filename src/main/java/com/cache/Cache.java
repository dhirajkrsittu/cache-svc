package com.cache;

import java.util.Map;

public interface Cache<K, V> {
    void put(K key, V val);
    V get(K key);
    void remove(K key);

    int size();
    Map<K, CacheEntry<K, V>> getCache();
    void invalidate();
}
