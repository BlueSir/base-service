package com.smc.local.cache;

import com.nsq.*;
import com.smc.notify.Notify;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Hashtable;
import java.util.List;

/**
 * 同步缓存工厂
 * User: qinqd
 * Date: 13-9-5
 * Time: 上午11:51
 * To change this template use File | Settings | File Templates.
 */
public class TimedCacheFactory<K, V> {
    private final static Logger logger = LoggerFactory.getLogger(TimedCacheFactory.class.getName());
    public final static Hashtable<String, TimedCacheFactory> INSTANCE_LIST = new Hashtable<String, TimedCacheFactory>();
    public final static Hashtable<String, NSQConsumer> CONSUMER_LIST = new Hashtable<String, NSQConsumer>();

    private final String module;
    private TimedCache timedCache;
    private boolean needSync;
    private NSQProducer producer;
    private String topic;
    private RemoveCallBack<K, V> removeCallBack;

    /**
     * 非同步清理缓存的构造方法
     * @param module
     * @param capacity
     * @param expireInterval
     */
    private TimedCacheFactory(final String module, int capacity, long expireInterval) {
        this.timedCache = new TimedCache<K, V>(capacity, expireInterval);
        this.module = module;
        this.needSync = false;
    }

    /**
     * 同步清理缓存的构造方法，以监听NSQ指定的topic的方式同步清理
     * @param module
     * @param capacity
     * @param expireInterval
     * @param topic
     */
    private TimedCacheFactory(String module, int capacity, long expireInterval, String topic) {
        this.timedCache = new TimedCache<K, V>(capacity, expireInterval);
        this.module = module;
        producer = Notify.producer;
        this.needSync = true;
        if(StringUtils.isNotBlank(topic)){
            this.topic = topic;
        } else {
            this.topic = "cache_clear";
        }
        startConsumer(this.topic);
    }

    /**
     * 同步清理缓存的构造方法，以监听NSQ指定的topic的方式同步清理
     * @param module
     * @param capacity
     * @param expireInterval
     * @param topic
     */
    private TimedCacheFactory(String module, int capacity, long expireInterval, String topic, RemoveCallBack callBack) {
        this.timedCache = new TimedCache<K, V>(capacity, expireInterval);
        this.module = module;
        producer = Notify.producer;
        this.needSync = true;
        this.removeCallBack = callBack;
        if(StringUtils.isNotBlank(topic)){
            this.topic = topic;
        } else {
            this.topic = "cache_clear";
        }
        startConsumer(this.topic, callBack);
    }

    /**
     * 非同步清理缓存的有分组功能的构造方法
     * @param module
     * @param capacity
     * @param expireInterval
     * @param isGroup
     */
    private TimedCacheFactory(final String module, int capacity, long expireInterval, boolean isGroup) {
        this.timedCache = new TimedCache<K, V>(capacity, expireInterval, true);
        this.module = module;
        this.needSync = false;
    }

    /**
     * 同步清理缓存的有分组功能的构造方法，以监听NSQ指定的topic的方式同步清理
     * @param module
     * @param capacity
     * @param expireInterval
     * @param topic
     * @param isGroup
     */
    private TimedCacheFactory(final String module, int capacity, long expireInterval, boolean isGroup, String topic) {
        this.timedCache = new TimedCache<K, V>(capacity, expireInterval, true);
        this.module = module;
        this.needSync = true;
        producer = Notify.producer;
        if(StringUtils.isNotBlank(topic)){
            this.topic = topic;
        } else {
            this.topic = "cache_clear";
        }
        startConsumer(this.topic);
    }

    /**
     * 获取非同步清理缓存的实例
     * @param moduleName 缓存的实例名称
     * @param capacity  维护的记录的条数
     * @param expireInterval 缓存失效的时长
     */
    public static synchronized TimedCacheFactory getInstance(String moduleName, int capacity, long expireInterval) {
        TimedCacheFactory instance = INSTANCE_LIST.get(moduleName);
        if (instance == null) {
            instance = new TimedCacheFactory(moduleName, capacity, expireInterval);
            INSTANCE_LIST.put(moduleName, instance);
            System.out.println("INFO: TimedCacheFactory.getInstance created:" + moduleName + " module_count=" + INSTANCE_LIST.size());
        }
        return instance;
    }

    /**
     * 获取同步清理缓存的实例
     * @param moduleName 缓存的实例名称
     * @param capacity  维护的记录的条数
     * @param expireInterval 缓存失效的时长
     * @param topic  监听消息中心的topic
     * @return
     */
    public static synchronized TimedCacheFactory getInstance(String moduleName, int capacity, long expireInterval, String topic) {
        TimedCacheFactory instance = INSTANCE_LIST.get(moduleName);
        if (instance == null) {
            instance = new TimedCacheFactory(moduleName, capacity, expireInterval, topic);
            INSTANCE_LIST.put(moduleName, instance);

            System.out.println("INFO: TimedCacheFactory.getInstance created:" + moduleName + " module_count=" + INSTANCE_LIST.size());
        }
        return instance;
    }

    /**
     * 获取同步清理缓存的实例
     * @param moduleName 缓存的实例名称
     * @param capacity  维护的记录的条数
     * @param expireInterval 缓存失效的时长
     * @param topic  监听消息中心的topic
     * @return
     */
    public static synchronized TimedCacheFactory getInstance(String moduleName, int capacity, long expireInterval, String topic, RemoveCallBack callBack) {
        TimedCacheFactory instance = INSTANCE_LIST.get(moduleName);
        if (instance == null) {
            instance = new TimedCacheFactory(moduleName, capacity, expireInterval, topic, callBack);
            INSTANCE_LIST.put(moduleName, instance);

            System.out.println("INFO: TimedCacheFactory.getInstance created:" + moduleName + " module_count=" + INSTANCE_LIST.size());
        }
        return instance;
    }

    /**
     * 获取同步清理缓存的有分组功能的实例
     * @param moduleName 缓存的实例名称
     * @param capacity  维护的记录的条数
     * @param expireInterval 缓存失效的时长
     * @param topic  监听消息中心的topic
     * @return
     */
    public static synchronized TimedCacheFactory getGroupInstance(String moduleName, int capacity, long expireInterval, String topic) {
        TimedCacheFactory instance = INSTANCE_LIST.get(moduleName);
        if (instance == null) {
            instance = new TimedCacheFactory(moduleName, capacity, expireInterval, true, topic);
            INSTANCE_LIST.put(moduleName, instance);

            System.out.println("INFO: TimedCacheFactory.getInstance created:" + moduleName + " module_count=" + INSTANCE_LIST.size());
        }
        return instance;
    }

    /**
     *
     * 获取非同步清理缓存的有分组功能的实例
     * @param moduleName 缓存的实例名称
     * @param capacity  维护的记录的条数
     * @param expireInterval 缓存失效的时长
     * @return
     */
    public static synchronized TimedCacheFactory getGroupInstance(String moduleName, int capacity, long expireInterval) {
        TimedCacheFactory instance = INSTANCE_LIST.get(moduleName);
        if (instance == null) {
            instance = new TimedCacheFactory(moduleName, capacity, expireInterval, true);
            INSTANCE_LIST.put(moduleName, instance);

            System.out.println("INFO: TimedCacheFactory.getInstance created:" + moduleName + " module_count=" + INSTANCE_LIST.size());
        }
        return instance;
    }

    public TimedCache getTimedCache() {
        return timedCache;
    }

    /**
     * 放置对象到本地缓存
     * @param key
     * @param value
     * @return
     */
    public V put(K key, V value) {

        V v = (V) timedCache.put(key, value);
        return v;
    }

    /**
     * 放置对象到带分组功能的本地缓存
     * @param key
     * @param value
     * @param group
     * @return
     */
    public V put(K key, V value, CacheGroup group) {
        return (V) timedCache.put(key, value, group);
    }
    /**
     * 获取对象
     */
    public V get(K key) {
        return (V) timedCache.get(key);
    }

    /**
     * 打开监控功能
     * @param isEnable
     */
    public void setEnableStats(boolean isEnable){
        timedCache.setEnableStats(isEnable);
    }

    /**
     * 获取分组
     * @param key
     * @return
     */
    public CacheGroup getGroup(String key){
        return timedCache.getGroup(key);
    }

    /**
     * 刷新分组，使这一组缓存都失效
     * @param key
     */
    public void refrushGroup(String key){
        timedCache.refrushGroup(key);
        if (needSync) {
            try {
                /***
                 * 生产的消息的格式为：{"t":"type","h":"host","p":"port","m":"message"}
                 * 其中，type用来标识缓存清理的动作，目前有两个动作:
                 *      D:删除本地缓存
                 *      R:刷新分组缓存的分组，使整个组的缓存都失效
                 */
                Message message = new Message(MessageType.LOCAL_CACHE_REFRUSH_GROUP, Notify.identification.getHost(), Notify.identification.getPort(), module + "@" + key);

                producer.produce(this.topic, message.toString().getBytes());
            } catch (Exception e) {
                logger.error("", e);
            }
        }
    }

    /**
     * 重新设置对应key的失效时间
     * @param key
     * @param expireInterval
     */
    public void expireInterval(K key, long expireInterval) {
        timedCache.expireInterval(key, expireInterval);
    }

    /**
     * 获取对象，并将该对象继续缓存expireInterval时长
     * @param key
     * @param expireInterval
     * @return
     */
    public V get(K key, long expireInterval) {
        return (V) timedCache.get(key, expireInterval);
    }

    /**
     * 删除一个key
     *
     * @param key
     */
    public V remove(K key) {
        if (needSync) {
            try {
                /***
                 * 生产的消息的格式为：{"t":"type","h":"host","p":"port","m":"message"}
                 * 其中，type用来标识缓存清理的动作，目前有两个动作:
                 *      D:删除本地缓存
                 *      R:刷新分组缓存的分组，使整个组的缓存都失效
                 */
                Message message = new Message(MessageType.LOCAL_CAHCE_DELETE, Notify.identification.getHost(), Notify.identification.getPort(), module + "@" + key);
                producer.produce(this.topic, message.toString().getBytes());
            } catch (Exception e) {
                logger.error("", e);
            }
        }
        V oldValue = (V) timedCache.remove(key);
        if(removeCallBack != null && oldValue != null){
            V newValue = removeCallBack.getItem(key);
            timedCache.put(key, newValue);
        }
        return oldValue;
    }

    /**
     * 清空所有key
     */
    public void removeAll() {
        timedCache.removeAll();
    }

    public int size() {
        return timedCache.size();
    }
    public long hitCount() {
        return timedCache.hitCount();
    }
    public long missCount() {
        return timedCache.missCount();
    }
    public List<K> listKeys() {
        return timedCache.listKeys();
    }

    /**
     * 启动消费线程，一直监听topic
     * @param topic
     */
    public static void startConsumer(final String topic) {
        startConsumer(topic, null);
    }
    /**
     * 启动消费线程，一直监听topic
     * @param topic
     */
    public static void startConsumer(final String topic, final RemoveCallBack callBack) {
        Consumers.registerTopic(topic, new ConsumerListener() {
            @Override
            public boolean excute(Message message) {
                try {
                    String action = message.getT();
                    Host host = new Host(message.getH(), message.getP());
                    String type = message.getT();
                    MessageType messageType = MessageType.getMessageTypeByCode(type);
                    if (messageType == null) {
                        logger.error("Message type is not defined.messageType=" + type);
                        return true;
                    }
                    String[] msgs = StringUtils.split(message.getM(), "@");
                    if (msgs.length < 2) {
                        return true;
                    }
                    String moduleName = msgs[0];
                    String key = msgs[1];
                    TimedCacheFactory instance = INSTANCE_LIST.get(moduleName);
                    if (StringUtils.equals(host.toString(), Notify.identification.toString())) return true;
                    if (instance == null) return true;

                    //删除本地缓存
                    switch (messageType) {
                        case LOCAL_CAHCE_DELETE: {
                            //如果传了RemoveCallBack，则查询最新的对象，然后覆写该条本地缓存
                            TimedCache cache = instance.getTimedCache();
                            if (callBack != null) {
                                if (cache.get(key) != null) {
                                    Object item = callBack.getItem(key);
                                    if (item != null) cache.put(key, item);
                                }
                            } else {
                                cache.remove(key);
                            }
                            break;
                        }
                        case LOCAL_CACHE_REFRUSH_GROUP: {
                            //刷新分组缓存的分组
                            instance.getTimedCache().refrushGroup(key);
                            break;
                        }
                    }
                } catch (Exception e) {
                    logger.error("[TimedCacheFactory.startConsumer]:Consume nsq message error.", e);
                    return false;
                }
                return true;
            }
        }, 5);
    }

    public static void main(String[] args) throws Exception {
        Notify.init(7080);
        TimedCacheFactory timedCacheFactory = TimedCacheFactory.getInstance("u", 100, 1000 * 60);
        //timedCacheFactory.startConsumer();
        timedCacheFactory.put("abc", "ss");

        //timedCacheFactory.getTimedCache().remove("abc");
        //Thread.sleep(1000);
        System.out.println(timedCacheFactory.get("abc"));
        System.out.println(Notify.identification);
    }
}
