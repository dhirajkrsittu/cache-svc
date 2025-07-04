package com.cache;

import java.util.Iterator;
import java.util.Map;

// This strategy expires an entry if it has been more than TTL since it was last accessed
// Another strategy can be created similarly to expire by comparing time passed since creation
public class ExpirationStrategyImpl<K, V> implements ExpirationStrategy<K, V> {

    // if ttl <= 0 then keys will never expire
    private final long ttl;

    public ExpirationStrategyImpl(long ttl) {
        this.ttl = ttl;
    }

    @Override
    public void removeExpiredEntries(Cache<K, V> cache) {
        if (ttl <= 0) {
            return;
        }
        System.out.println("Expiring entries...");
        long currTime = System.currentTimeMillis();

        Iterator<Map.Entry<K, CacheEntry<K, V>>> iterator =
                cache.getCache().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<K, CacheEntry<K, V>> mapEntry = iterator.next();
            CacheEntry<K, V> cacheEntry = mapEntry.getValue();
            if (currTime - cacheEntry.getLastAccessedAt() > ttl) {
                System.out.println("Expiring entry with key " + cacheEntry.getKey());
                iterator.remove();
            }
        }
    }

    @Override
    public boolean isExpired(Cache<K, V> cache, CacheEntry<K, V> entry) {
        boolean expired = ttl > 0 && System.currentTimeMillis() - entry.getLastAccessedAt() > ttl;
        if (expired) {
            System.out.println("entry is expired with key " + entry.getKey());
            cache.remove(entry.getKey());
        }
        return expired;
    }

}
