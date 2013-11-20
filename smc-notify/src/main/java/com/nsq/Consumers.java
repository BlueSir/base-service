package com.nsq;

import com.smc.notify.Notify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 10/17/13
 * Time: 11:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class Consumers {
    private static final Hashtable<String, NSQConsumer> CONSUMER_LIST = new Hashtable<String, NSQConsumer>();
    private static final HashMap<String, Set<ConsumerListener>> LISTENER_LIST = new HashMap<String,Set<ConsumerListener>>();
    private static final Logger LOG = LoggerFactory.getLogger(Consumers.class);
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(){

            @Override
            public void run() {
                Iterator<String> it = CONSUMER_LIST.keySet().iterator();
                while(it.hasNext()){
                    String topic = it.next();
                    NSQConsumer each = CONSUMER_LIST.get(topic);
                    each.cleanupOldConnections();
                    each.close();
                }
            }
        });
    }

    /**
     * 注册订阅模式的监听
     * @param topic　订阅的主题
     * @param listener　监听器
     */
    public synchronized static void registerTopic(final String topic, ConsumerListener listener){
        register(topic, Notify.identification.toString(), listener);
    }

    /**
     * 注册队列模式的监听，所有订阅过该主题的监听者是一个组
     * @param topic　订阅的主题
     * @param listener　监听器
     */
    public synchronized static void registerQuene(final String topic, ConsumerListener listener){
        register(topic, "channel_" + topic, listener);
    }

    /**
     *  注册队列模式的监听，可以自定义组来监听
     * @param topic　订阅的主题
     * @param group　自定义组的名称，group相同的监听者是一组，组内只能消费一次
     * @param listener　监听器
     */
    public synchronized static void register(final String topic, String group, ConsumerListener listener){
        Set<ConsumerListener> consumers = LISTENER_LIST.get(topic);
        if(consumers == null){
            consumers = new HashSet<ConsumerListener>();
        }
        consumers.add(listener);
        LISTENER_LIST.put(topic, consumers);
        if(!CONSUMER_LIST.contains(topic)){
            NSQConsumer consumer = new NSQConsumer(Notify.lookup, topic, group, new NSQMessageCallback(){
                @Override
                public void message(NSQMessage message) {
                    if(message == null || message.getMessage() == null) {
                        message.finished();
                        return;
                    }
                    Message msg = Message.convert(new String(message.getMessage()));

                    LOG.info("[NSQCousumer]:NSQMessage="+msg);
                    Set<ConsumerListener> listeners = LISTENER_LIST.get(topic);
                    if(listeners != null){
                        for(ConsumerListener each : listeners){
                            if(!each.excute(msg)){
                                message.requeue();
                                return;
                            };
                        }
                    }
                    message.finished();
                }

                @Override
                public void error(Exception x) {
                    x.printStackTrace();
                }
            });
            consumer.start();
            CONSUMER_LIST.put(topic, consumer);
        }
    }
}
