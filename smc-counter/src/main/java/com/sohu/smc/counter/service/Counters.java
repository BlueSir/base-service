package com.sohu.smc.counter.service;

import com.smc.local.cache.TimedCache;
import com.sohu.smc.counter.model.Counter;
import com.sohu.smc.counter.model.CounterKey;
import com.sohu.smc.counter.model.CounterPrimaryKeyException;
import org.apache.commons.lang.StringUtils;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 10/15/13
 * Time: 11:00 AM
 * To change this template use File | Settings | File Templates.
 */
public enum Counters {
    CLIENT_SUB_COUNTER("CLIENT_SUB_COUNTER","C_CLIENT_SUB_", true, 10000, 30 * 1000),
    CLIENT_CLOUD_COUNTER("CLIENT_CLOUD_COUNTER","C_CLIENT_CLOUD_");

    String counterName;
    String prefix;
    boolean needCache = false;
    TimedCache<String, Long> COUNTER_CACHE = null;
    Counters(String counterName, String prefix, boolean needCache, int cacheCapacity, long cacheExpire){
        this.counterName = counterName;
        this.prefix = prefix;
        this.needCache = needCache;
        if(needCache){
            COUNTER_CACHE = new TimedCache<String, Long>(cacheCapacity, cacheExpire);
        }
    }
    Counters(String counterName, String prefix){
        this.counterName = counterName;
        this.prefix = prefix;
    }

    public long incr(CounterKey counterKey){
        //Counter incr
        try {
            return Counter.COUNTER.incr(this.prefix + counterKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public long incrBy(CounterKey counterKey, long increment){
        //Counter incr by increment
        try {
            return Counter.COUNTER.incrBy(this.prefix + counterKey , increment);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public long decr(CounterKey counterKey){
        //Counter decr
        try {
            return Counter.COUNTER.decr(this.prefix + counterKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public long decrBy(CounterKey counterKey, long decrement){
        //Counter decr by decrement
        return Counter.COUNTER.decrBy(this.prefix + counterKey, decrement);
    }
    public boolean reset(CounterKey counterKey, long count){
        String ret = Counter.COUNTER.set(this.prefix + counterKey, count+"");
        if(StringUtils.isNotBlank(ret)) return true;
        return false;
    }

    public long getCount(CounterKey counterKey){
        Long count = new Long(-1);
        if(needCache && COUNTER_CACHE != null){
            count = COUNTER_CACHE.get(counterKey.toString());
            if(count != null){
                return count.longValue();
            }
        }
        String ret = Counter.COUNTER.get(this.prefix + counterKey);
        if(StringUtils.isNotBlank(ret)){
            try{
                count = Long.valueOf(ret);
            } catch (Exception e){
                count = new Long(-1);
            }
        }
        if(needCache && COUNTER_CACHE != null){
            COUNTER_CACHE.put(counterKey.toString(), count);
        }


        return count.longValue();
    }

}
