package com.smc.lively.service;

import com.smc.lively.enums.LivelyItemEnum;
import com.sohu.smc.redis.SmcJedis;
import com.sohu.smc.redis.SmcJedisFactory;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 2/25/14
 * Time: 5:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class LivelyRedis {
    static SmcJedis jedis = SmcJedisFactory.getInstance("lively");
    static final String KEY = "lively_%s";
    static final Logger LOG = Logger.getLogger(LivelyRedis.class);
    public static long add(LivelyItemEnum liveEnum, long item){
        String key = String.format(KEY, liveEnum.name);
        long ret = jedis.zadd(key, liveEnum.getScore(), item+"");
        return ret;
    }

    public static Set<Long> getAllLively(LivelyItemEnum livelyItemEnum) {
        Set<String> livelyFromRedis = jedis.zrangeByScore(String.format(KEY, livelyItemEnum.name), livelyItemEnum.getMinScore(), livelyItemEnum.getMaxScore());
        Set<Long> lively = new HashSet<Long>();
        for(String each : livelyFromRedis){
            lively.add(Long.valueOf(each));
        }
        return lively;
    }

    public static Set<String> livelyOverdue(LivelyItemEnum livelyItemEnum){
        String key = String.format(KEY, livelyItemEnum.name);
        Set<String> overdue = jedis.zrangeByScore(key, 0, livelyItemEnum.getOverdueScore());

        if(overdue != null && overdue.size() >0 ){
            long ret = jedis.zremrangeByScore(key, 0, livelyItemEnum.getOverdueScore());
            if(ret <= 0){
                LOG.error("[livelyOverdue]:Remove overdue lively error.");
            }
        }
        return overdue;
    }

    public static boolean getLock(LivelyItemEnum livelyItemEnum){
        String key = String.format("lively_lock_", livelyItemEnum.name);
        long ret = jedis.sadd(key, "1");
        if(ret == 1){
            jedis.expire(key, livelyItemEnum.getLockExpire());
            return true;
        }else{
            return false;
        }
    }
}
