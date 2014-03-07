package com.smc.lively.collect;

import com.smc.lively.enums.LivelyItemEnum;
import com.smc.lively.kafka.KafkaEnum;
import com.smc.lively.kafka.KafkaProducer;
import com.smc.lively.kafka.KafkaProducerFactory;
import com.smc.lively.service.LivelyRedis;
import com.smc.local.cache.TimedCacheFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 2/24/14
 * Time: 4:34 PM
 */
public class LivelyCollect {

    static final Logger LOG = LoggerFactory.getLogger(LivelyCollect.class);
    static TimedCacheFactory<String, Byte> lively_cache = TimedCacheFactory.getInstance("LIVELY_CACHE", 1000000, 60 * 60 * 2);
    static KafkaProducer producer = KafkaProducerFactory.getProducer("lively");

    public static void collect(LivelyItemEnum livelyItemEnum, long item){
        String key = livelyItemEnum.name + "_" + item;
        Byte b = lively_cache.get(key);
        if(b == null){
            long ret = LivelyRedis.add(livelyItemEnum, item);
            if(ret == 1){
                //通知新增加了一个活跃用户     \
                StringBuilder sb = new StringBuilder(livelyItemEnum.name).append("@").append(item);
                producer.send(KafkaEnum.LIVELY_ADD_TOPIC.name, sb.toString());
                LOG.info("[collect]:producer send message:" + sb.toString());
            }
            lively_cache.put(key, (byte)1);
        }
    }
}
