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
     * 注册订阅模式的监听
     * @param topic　订阅的主题
     * @param listener　监听器
     * @param retry  消息消费失败后，消息重发的次数，如果为 -1 则一直重发
     */
    public synchronized static void registerTopic(final String topic, ConsumerListener listener, int retry){
        register(topic, Notify.identification.toString(), listener, retry);
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
     * 注册队列模式的监听，所有订阅过该主题的监听者是一个组
     * @param topic　订阅的主题
     * @param group  消息该消息的组，组内只消费一次
     * @param listener　监听器
     * @param retry  消息消费失败后，消息重发的次数，如果为 -1 则一直重发
     */
    public synchronized static void registerQuene(final String topic, String group, ConsumerListener listener, int retry){
        register(topic, "channel_" + group + topic, listener, retry);
    }

    /**
     * 注册队列模式的监听，所有订阅过该主题的监听者是一个组
     * @param topic　订阅的主题
     * @param group  消息该消息的组，组内只消费一次
     * @param listener　监听器
     */
    public synchronized static void registerQuene(final String topic, String group, ConsumerListener listener){
        register(topic, "channel_" + group + topic, listener);
    }

    /**
     * 注册队列模式的监听，所有订阅过该主题的监听者是一个组
     * @param topic　订阅的主题
     * @param listener　监听器
     * @param retry  消息消费失败后，消息重发的次数，如果为 -1 则一直重发
     */
    public synchronized static void registerQuene(final String topic, ConsumerListener listener, int retry){
        register(topic, "channel_" + topic, listener, retry);
    }
    /**
     *  注册队列模式的监听，可以自定义组来监听
     * @param topic　订阅的主题
     * @param group　自定义组的名称，group相同的监听者是一组，组内只能消费一次
     * @param listener　监听器
     */
    public synchronized static void register(final String topic, String group, ConsumerListener listener){
        register(topic, group, listener, -1);
    }
    /**
     *  注册队列模式的监听，可以自定义组来监听
     * @param topic　订阅的主题
     * @param group　自定义组的名称，group相同的监听者是一组，组内只能消费一次
     * @param listener　监听器
     * @param retry  消息消费失败后，消息重发的次数，如果为 -1 则一直重发
     */
    public synchronized static void register(final String topic, String group, ConsumerListener listener, final int retry){
        register(Notify.lookup, topic, group, listener, retry);

    }
    public synchronized static void register(NSQLookup lookup, final String topic, String group, ConsumerListener listener, final int retry){

        Set<ConsumerListener> consumers = LISTENER_LIST.get(topic);
        if(consumers == null){
            consumers = new HashSet<ConsumerListener>();
        }
        consumers.add(listener);
        LISTENER_LIST.put(topic, consumers);
        if(!CONSUMER_LIST.contains(topic)){
            NSQConsumer consumer = new NSQConsumer(lookup, topic, group, new NSQMessageCallback(){
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
                                if(message.getAttempts() > retry && retry > 0){
                                    //如果达到重发的次数，则将该消息丢失
                                    LOG.info("[SmcNSQConsumer]:Retry count enough.msg="+msg+",retry="+retry + ",attempts="+message.getAttempts());
                                    message.finished();
                                }else{
                                    LOG.info("[SmcNSQConsumer]:Requeue message.msg="+msg+",retry="+retry + ",attempts="+message.getAttempts());
                                    message.setMessage(msg.toString().getBytes());
                                    message.requeue();
                                }
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
