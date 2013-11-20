package com.smc.local.cache;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A timed LRU cache.  Items remain valid until they expire.
 * TimedCache can simplify database caching.
 * <p/>
 * <pre><code>
 * TimedCache storyCache = new TimedCache(30, 60000);
 * <p/>
 * public Story getCurrentStory(String id)
 * {
 *   Story story = (Story) storyCache.get(id);
 * <p/>
 *   if (story == null) {
 *     story = DB.queryStoryDatabase(id);
 *     storyCache.put(id, story);
 *   }
 * <p/>
 *   return story;
 * }
 * </code></pre>
 */
public class TimedCache<K, V> {
    private LruCache<K, Entry<V>> _cache;
    private LruCache<String, CacheGroup> _groupCache;
    private long _expireInterval;

    /***
     * Creates a new timed LRU cache.<br>
     * 注意：单位是毫秒
     *
     * @param capacity
     *            the maximum size of the LRU cache
     * @param expireInterval
     *            the time an entry remains valid
     */
    public TimedCache(int capacity, long expireInterval) {
        _cache = new LruCache<K, Entry<V>>(capacity);
        _expireInterval = expireInterval;
    }

    public TimedCache(int capacity, long expireInterval, boolean isGroup) {
        this(capacity, expireInterval);
        if(isGroup){
            _groupCache = new LruCache<String, CacheGroup>(capacity);
        }
    }

    public void setEnableStats(boolean isEnable){
        _cache.setEnableStatistics(isEnable);
    }

    /***
     * Put a new item in the cache.
     */
    public V put(K key, V value) {
        Entry<V> oldValue = _cache.put(key, new Entry<V>(_expireInterval, value));

        if (oldValue != null)
            return oldValue.getValue();
        else
            return null;
    }

    /***
     * Put a new item in the cache.
     */
    public V put(K key, V value, CacheGroup group) {
        Entry<V> oldValue = _cache.put(key, new Entry<V>(_expireInterval, value, group));

        if (oldValue != null)
            return oldValue.getValue();
        else
            return null;
    }

    public CacheGroup getGroup(String key){
        CacheGroup group = _groupCache.get(key);
        if(group == null){
            group = new CacheGroup(key, System.currentTimeMillis());
            _groupCache.put(key, group);
        }
        return group;
    }

    public void refrushGroup(String key){
        CacheGroup group = new CacheGroup(key, System.currentTimeMillis());
        _groupCache.put(key, group);
    }
    /***
     * Gets an item from the cache, returning null if expired.
     */
    public V get(K key) {
        Entry<V> entry = _cache.get(key);

        if (entry == null)
            return null;
        if(entry._group != null){
            CacheGroup group = this.getGroup(entry._group.group);
            if(entry.isValid() && entry.isValid(group)){
                return entry.getValue();
            }else{
                _cache.remove(key);
                return null;
            }
        }else{

            if (entry.isValid())
                return entry.getValue();
            else {
                _cache.remove(key);
                return null;
            }
        }
    }

    /***
     * Set expire interval to key.
     */
    public void expireInterval(K key, long expireInterval) {
        Entry<V> entry = _cache.get(key);

        if (entry == null)
            return;

        entry.setExpireInterval(expireInterval);
    }

    /***
     * Gets an item from the cache, and remains interval.
     */
    public V get(K key, long expireInterval) {
        Entry<V> entry = _cache.get(key);

        if (entry == null)
            return null;

        entry.setExpireInterval(expireInterval);
        return entry.getValue();
    }

    /**
     * 删除一个key
     *
     * @param key
     */
    public V remove(K key) {
        Entry<V> entry = _cache.remove(key);
        if(entry == null) return null;
        return entry.getValue();
    }

    /**
     * 清空所有key
     */
    public void removeAll() {
        _cache.clear();
    }

    public int size() {
        return _cache.size();
    }
    public long hitCount() {
        return _cache.getHitCount();
    }
    public long missCount() {
        return _cache.getMissCount();
    }
    public List<K> listKeys() {
        List<K> keys = new ArrayList<K>();
        Iterator<K> ki = _cache.keys();
        while (ki.hasNext()) {
            keys.add(ki.next());
        }
        return keys;
    }

    /***
     * Class representing a cached entry.
     */
    static class Entry<V> implements CacheListener {
        private long _expireInterval;
        private long _checkTime;
        private V _value;
        private CacheGroup _group;

        Entry(long expireInterval, V value) {
            _expireInterval = expireInterval;
            _value = value;

            _checkTime = System.currentTimeMillis();
        }

        Entry(long expireInterval, V value , CacheGroup group) {
            this(expireInterval,value);
            _group = group;
        }

        boolean isValid() {
            return System.currentTimeMillis() < _checkTime + _expireInterval;
        }

        boolean isValid(CacheGroup group){
            if(this._group != null){
                return this._group.equals(group);
            }else{
                return System.currentTimeMillis() < _checkTime + _expireInterval;
            }
        }

        V getValue() {
            return _value;
        }

        public void removeEvent() {
            if (_value instanceof CacheListener)
                ((CacheListener) _value).removeEvent();
        }

        public void setExpireInterval(long expireInterval){
            this._expireInterval = expireInterval;
            _checkTime = System.currentTimeMillis();
        }
    }
}

