package com.smc.lively.collect;

import com.smc.lively.enums.LivelyItemEnum;
import com.smc.lively.kafka.KafkaProducer;
import com.smc.lively.kafka.KafkaProducerFactory;
import com.smc.lively.service.LivelyRedis;
import com.smc.local.cache.TimedCacheFactory;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 2/24/14
 * Time: 4:34 PM
 */
public class LivelyCollect {

    static TimedCacheFactory<String, Byte> lively_cache = TimedCacheFactory.getInstance("LIVELY_CACHE", 1000000, 60 * 60 * 6);
    static KafkaProducer producer = KafkaProducerFactory.getProducer("lively");

    public static void collect(LivelyItemEnum livelyItemEnum, long item){
        String key = livelyItemEnum.name + "_" + item;
        Byte b = lively_cache.get(key);
        if(b == null){
            long ret = LivelyRedis.add(livelyItemEnum, item);
            if(ret == 1){
                //通知新增加了一个活跃用户
                producer.send("lively_add", item+"");
            }
            lively_cache.put(key, (byte)1);
        }
    }
}
