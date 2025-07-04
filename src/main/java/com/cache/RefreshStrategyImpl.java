package com.cache;

public class RefreshStrategyImpl<K, V> implements RefreshStrategy<K, V> {

    // if refreshTime <= 0 then we never refresh the entries
    private final long refreshTime;

    public RefreshStrategyImpl(long refreshTime) {
        this.refreshTime = refreshTime;
    }

    @Override
    public void refresh(CacheEntry<K, V> cacheEntry) {
        if (refreshTime > 0 && System.currentTimeMillis() - cacheEntry.getLastAccessedAt() > refreshTime) {
            System.out.println("item to be refreshed with a new value from the backing store for key " +
                    cacheEntry.getKey().toString());

            /* logic to refresh cache data from backing store
                V newVal = get the latest value from backing store
                cacheEntry.setVal(newVal);
            */
        }
    }
}
