package com.smc.lively.service;

import com.smc.lively.enums.LivelyItemEnum;
import com.sohu.smc.redis.SmcJedis;
import com.sohu.smc.redis.SmcJedisFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
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
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
    public static long add(LivelyItemEnum liveEnum, long item){
        String dateFormat = sdf.format(new Date());
        long ret = jedis.zadd("lively_" + liveEnum.name, Double.valueOf(dateFormat), item+"");
        return ret;
    }


    public static Set<Long> getAllLively(LivelyItemEnum livelyItemEnum) {

        return null;
    }
}
