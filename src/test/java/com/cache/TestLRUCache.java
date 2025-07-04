package com.cache;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestLRUCache {

    private final Cache<String, String> cache;

    public TestLRUCache() {
        int capacity = 2;
        long ttl = 0;
//        long ttl = 4000;
        long refreshTime = 0;
//        long refreshTime = 1000;

        ExpirationStrategy<String, String> expirationStrategy = new ExpirationStrategyImpl<>(ttl);
        RefreshStrategy<String, String> refreshStrategy = new RefreshStrategyImpl<>(refreshTime);
        this.cache = new LRUCacheImpl<>(capacity, expirationStrategy, refreshStrategy, WritePolicy.WRITE_THROUGH);
    }

    @Test
    public void testCase1() {
        cache.put("key1", "value1");
        assertEquals("value2", cache.get("key1"));
    }

    @Test
    public void testCase2() {
        assertNull(cache.get("keyX"));
    }

    @Test
    public void testCase3() {
        cache.put("key1", "value1");
        cache.put("key1", "value2");
        assertEquals("value2", cache.get("key1"));
    }

    @Test
    public void testCase4() {
        cache.put("key1", "value1");
        cache.remove("key1");
        assertNull(cache.get("key1"));
    }

    @Test
    public void testCase5() {
        cache.put("key1", "value1");
        cache.put("key2", "value2");
        assertNotNull(cache.get("key1"));
        cache.put("key3", "value3");
        assertNull(cache.get("key2"));

        cache.invalidate();
        assertEquals(0, cache.size());
    }

    @Test
    public void testCase6() throws InterruptedException {
        cache.put("key1", "value1");
        cache.put("key2", "value2");
        cache.put("key3", "value3");
//        assertNull(cache.get("key1"));
        Thread.sleep(1000);
        System.out.println(cache.get("key1"));
        System.out.println(cache.size());
        Thread.sleep(6000);
        System.out.println(cache.size());
    }
}
