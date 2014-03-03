package com.smc.lively.kafka;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 2/25/14
 * Time: 11:52 AM
 * To change this template use File | Settings | File Templates.
 */
public class KafkaProducerFactory {
    private static ConcurrentHashMap<String, KafkaProducer> PRODUCER_MAP = new ConcurrentHashMap<String, KafkaProducer>();

    public static KafkaProducer getProducer(String module){
        if(PRODUCER_MAP.containsKey(module)){
            return PRODUCER_MAP.get(module);
        } else {
            KafkaProducer producer = new KafkaProducer(module);
            PRODUCER_MAP.put(module, producer);
            return producer;
        }
    }
}
