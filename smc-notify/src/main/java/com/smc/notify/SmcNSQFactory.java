package com.smc.notify;

import com.nsq.NSQLookup;
import com.nsq.NSQProducer;
import com.nsq.lookup.NSQLookupDynMapImpl;
import com.smc.notify.config.SmcNsqConfig;
import com.sohu.smc.config.model.AppConfiguration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 12/23/13
 * Time: 2:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class SmcNSQFactory {
    static final Logger LOG = LoggerFactory.getLogger(SmcNSQFactory.class);
    static ConcurrentHashMap<String, SmcNSQProducer> PRODUCER_MAP = new ConcurrentHashMap<String, SmcNSQProducer>();
    static ConcurrentHashMap<String, SmcNSQConsumer> CONSUMER_MAP = new ConcurrentHashMap<String, SmcNSQConsumer>();
    static ConcurrentHashMap<String, NSQLookup> NSQ_LOOKUP_MAP = new ConcurrentHashMap<String, NSQLookup>();
    final static String NSQ_PRODUCER_HOST = "smc.nsq.%s.producer.host";
    final static String NSQ_LOOKUP_HOST = "smc.nsq.%s.lookup.host";

    public static SmcNSQConsumer getConsumer(String module){
        if(CONSUMER_MAP.containsKey(module)){
            return CONSUMER_MAP.get(module);
        } else {
            SmcNSQConsumer smcNSQConsumer = new SmcNSQConsumer(createNSQLookup(module));
            CONSUMER_MAP.put(module, smcNSQConsumer);
            return smcNSQConsumer;
        }
    }

    public static SmcNSQProducer getProducer(String module){
        return getProducer(module, 1);
    }

    public static SmcNSQProducer getProducer(String module, int poolSize){
        if(PRODUCER_MAP.containsKey(module)){
            return PRODUCER_MAP.get(module);
        } else {
            NSQProducer nsqProducer = new NSQProducer();
            String nsqProducerHost = AppConfiguration.getString(String.format(NSQ_PRODUCER_HOST, module), "").get();
            String[] hosts = StringUtils.split(nsqProducerHost, ",");
            for(String host : hosts){
                String[] url = StringUtils.split(host, ":");
                if(url.length != 2) {
                    LOG.error("[smc-notify]:NSQ Producer host url format error.url="+host);
                    System.exit(0);
                    break;
                }else{
                    String ip = url[0];
                    int port = Integer.parseInt(url[1]);
                    nsqProducer.addAddress(ip, port, poolSize);
                }
            }
            nsqProducer.start();
            SmcNSQProducer smcNSQProducer = new SmcNSQProducer(nsqProducer, createNSQLookup(module));
            PRODUCER_MAP.put(module, smcNSQProducer);
            return smcNSQProducer;
        }
    }

    protected static NSQLookup createNSQLookup(String module){
        if(NSQ_LOOKUP_MAP.containsKey(module)){
            return NSQ_LOOKUP_MAP.get(module);
        }
        String nsqLookupHost = AppConfiguration.getString(String.format(NSQ_LOOKUP_HOST, module), "").get();
        if(StringUtils.isBlank(nsqLookupHost)) {
            LOG.error("[smc-notify]:NSQ Lookup host not config in smc-configuration.");
            System.exit(0);
        } else {
            NSQLookup lookup = new NSQLookupDynMapImpl();
            String[] hosts = StringUtils.split(nsqLookupHost, ",");
            for(String host : hosts){
                String[] url = StringUtils.split(host, ":");
                if(url.length != 2) {
                    LOG.error("[smc-notify]:NSQ Lookup host url format error.url="+host);
                    System.exit(0);
                    break;
                }else{
                    String ip = url[0];
                    int port = Integer.parseInt(url[1]);
                    lookup.addAddr(ip, port);
                }
            }
            NSQ_LOOKUP_MAP.put(module, lookup);
            return lookup;
        }
        return null;
    }

}
