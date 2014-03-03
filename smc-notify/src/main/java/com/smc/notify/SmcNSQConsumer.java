package com.smc.notify;

import com.nsq.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 12/23/13
 * Time: 2:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class SmcNSQConsumer {
    private final Hashtable<String, NSQConsumer> CONSUMER_LIST = new Hashtable<String, com.nsq.NSQConsumer>();
    private final HashMap<String, Set<ConsumerListener>> LISTENER_LIST = new HashMap<String,Set<ConsumerListener>>();
    private final Logger LOG = LoggerFactory.getLogger(Consumers.class);
    private NSQLookup nsqLookup;

    public SmcNSQConsumer(NSQLookup lookup){
        this.nsqLookup = lookup;
    }
    /**
     * 注册订阅模式的监听
     * @param topic　订阅的主题
     * @param listener　监听器
     */
    public synchronized void registerTopic(final String topic, ConsumerListener listener){
        register(topic, Notify.identification.toString(), listener);
    }

    /**
     * 注册订阅模式的监听
     * @param topic　订阅的主题
     * @param listener　监听器
     * @param retry  消息消费失败后，消息重发的次数，如果为 -1 则一直重发
     */
    public synchronized void registerTopic(final String topic, ConsumerListener listener, int retry){
        register(topic, Notify.identification.toString(), listener, retry);
    }

    /**
     * 注册队列模式的监听，所有订阅过该主题的监听者是一个组
     * @param topic　订阅的主题
     * @param listener　监听器
     */
    public synchronized void registerQuene(final String topic, ConsumerListener listener){
        register(topic, "channel_" + topic, listener);
    }

    /**
     * 注册队列模式的监听，所有订阅过该主题的监听者是一个组
     * @param topic　订阅的主题
     * @param group  消息该消息的组，组内只消费一次
     * @param listener　监听器
     * @param retry  消息消费失败后，消息重发的次数，如果为 -1 则一直重发
     */
    public synchronized void registerQuene(final String topic, String group, ConsumerListener listener, int retry){
        register(topic, "channel_" + group + topic, listener, retry);
    }

    /**
     * 注册队列模式的监听，所有订阅过该主题的监听者是一个组
     * @param topic　订阅的主题
     * @param group  消息该消息的组，组内只消费一次
     * @param listener　监听器
     */
    public synchronized void registerQuene(final String topic, String group, ConsumerListener listener){
        register(topic, "channel_" + group + topic, listener);
    }

    /**
     * 注册队列模式的监听，所有订阅过该主题的监听者是一个组
     * @param topic　订阅的主题
     * @param listener　监听器
     * @param retry  消息消费失败后，消息重发的次数，如果为 -1 则一直重发
     */
    public synchronized void registerQuene(final String topic, ConsumerListener listener, int retry){
        register(topic, "channel_" + topic, listener, retry);
    }
    /**
     *  注册队列模式的监听，可以自定义组来监听
     * @param topic　订阅的主题
     * @param group　自定义组的名称，group相同的监听者是一组，组内只能消费一次
     * @param listener　监听器
     */
    public synchronized void register(final String topic, String group, ConsumerListener listener){
        register(topic, group, listener, -1);
    }
    public synchronized void register(final String topic, String group, ConsumerListener listener, final int retry){

        Set<ConsumerListener> consumers = LISTENER_LIST.get(topic);
        if(consumers == null){
            consumers = new HashSet<ConsumerListener>();
        }
        consumers.add(listener);
        LISTENER_LIST.put(topic, consumers);
        if(!CONSUMER_LIST.contains(topic)){
            com.nsq.NSQConsumer consumer = new com.nsq.NSQConsumer(this.nsqLookup, topic, group, new NSQMessageCallback(){
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
