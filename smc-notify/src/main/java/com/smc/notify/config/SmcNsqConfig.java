package com.smc.notify.config;

import com.netflix.config.DynamicIntProperty;
import com.netflix.config.DynamicStringProperty;
import com.sohu.smc.config.model.AppConfiguration;

/**
 * Created with IntelliJ IDEA.
 * User: xiaocaijing
 * Date: 13-9-10
 * Time: 下午5:13
 */
public class SmcNsqConfig extends AppConfiguration {
    public static final DynamicStringProperty NSQ_NOTIFY_PRODUCER_HOST = getString("smc.nsq.producer.host", "");
    public static final DynamicIntProperty NSQ_NOTIFY_PRODUCER_PORT = getInt("smc.nsq.producer.port", 4150);
    public static final DynamicStringProperty NSQ_NOTIFY_HOST = getString("smc.nsq.host", "");
    public static final DynamicIntProperty NSQ_NOTIFY_PORT = getInt("smc.nsq.port", 4161);

}
