package com.cache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LRUCacheImpl<K, V> implements Cache<K, V> {
    private final int capacity;
    private Map<K, CacheEntry<K, V>> cacheMap;
    private final ExpirationStrategy<K, V> expirationStrategy;
    private final RefreshStrategy<K, V> refreshStrategy;
    private final ScheduledExecutorService scheduler;
    private final WritePolicy writePolicy;

    public LRUCacheImpl(
            int capacity,
            ExpirationStrategy<K, V> expirationStrategy,
            RefreshStrategy<K, V> refreshStrategy,
            WritePolicy writePolicy) {
        this.capacity = capacity;
        this.expirationStrategy = expirationStrategy;
        this.refreshStrategy = refreshStrategy;
        this.writePolicy = writePolicy;

        /* setting access order to true in a linked hash map so that
        most recently accessed entry is automatically moved to the end
         */
        // load factor is set to 1 so that map doesn't increase its size when full
        this.cacheMap = new LinkedHashMap<>(capacity, 1.0f, true);

        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        // running a background thread every 2 seconds to remove the expired items
        this.scheduler.scheduleAtFixedRate(() -> this.expirationStrategy.removeExpiredEntries(this), 1, 2, TimeUnit.SECONDS);
    }

    @Override
    public void put(K key, V val) {
        if (cacheMap.size() >= capacity) {
            evict(this);
        }
        cacheMap.put(key, new CacheEntry<>(key, val));

        // handling write policies
        switch (this.writePolicy) {
            case WRITE_THROUGH:
                writeToBackingStore(key, val);
                break;
            case WRITE_BACK:
                writeToBackingStoreAsync(key, val);
                break;
        }
    }

    @Override
    public V get(K key) {
        CacheEntry<K, V> entry = cacheMap.get(key);
        if (entry == null || expirationStrategy.isExpired(this, entry)) {
            // Retrieve from backing store or null if not found
            return null;
        }
        refreshStrategy.refresh(entry);
        entry.updateLastAccessedAt();
        return entry.getVal();
    }

    @Override
    public void remove(K key) {
        cacheMap.remove(key);
    }

    @Override
    public int size() {
        return cacheMap.size();
    }

    @Override
    public Map<K, CacheEntry<K, V>> getCache() {
        return this.cacheMap;
    }

    private void evict(Cache<K, V> cache) {
        if (cache.size() > 0) {
            // since most recently used entry is moved to the end, LRU entry will be at the front (or head)
            K leastRecentlyUsedKey = cache.getCache().keySet().iterator().next();
            cache.remove(leastRecentlyUsedKey);
        }
    }

    // this method can be called during application shutdown
    public void shutdown() {
        scheduler.shutdown();
    }

    private void writeToBackingStore(K key, V val) {
        // write data to backing store, this blocks the put operation until completed or failed
        System.out.println("updating data in backing store synchronously: key " + key.toString());
    }

    private void writeToBackingStoreAsync(K key, V val) {
        CompletableFuture.runAsync(() -> {
            // write data to backing store, this runs async and does not block the put operation
            System.out.println("updating data in backing store asynchronously, key " + key.toString());
        });
    }

    @Override
    public void invalidate() {
        this.cacheMap = new LinkedHashMap<>(this.capacity, 1.0f, true);
    }
}
