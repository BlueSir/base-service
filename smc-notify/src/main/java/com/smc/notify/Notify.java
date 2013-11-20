package com.smc.notify;

import com.nsq.Host;
import com.nsq.NSQLookup;
import com.nsq.NSQProducer;
import com.nsq.lookup.NSQLookupDynMapImpl;
import com.smc.notify.config.SmcNsqConfig;
import org.apache.commons.lang.StringUtils;

import java.net.*;
import java.util.Enumeration;

/**
 * Notify
 * User: qinqd
 * Date: 13-9-6
 * Time: 上午10:27
 * To change this template use File | Settings | File Templates.
 */
public class Notify {
    public static NSQProducer producer;
    public static NSQLookup lookup;
    public static Host identification = new Host(getLocalIP());
    public static InetAddress address;
    static{
        producer = new NSQProducer().addAddress(SmcNsqConfig.NSQ_NOTIFY_PRODUCER_HOST.get(), SmcNsqConfig.NSQ_NOTIFY_PRODUCER_PORT.get(), 1);
        producer.start();
        lookup = new NSQLookupDynMapImpl();
        lookup.addAddr(SmcNsqConfig.NSQ_NOTIFY_HOST.get(), SmcNsqConfig.NSQ_NOTIFY_PORT.get());
    }
    public static void init(int port){
        identification.setPort(port);
    }
    public static String getLocalIP() {
        String ip = "";
        try {
            Enumeration<?> e1 = (Enumeration<?>) NetworkInterface.getNetworkInterfaces();
            while (e1.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) e1.nextElement();
                if (!ni.getName().equals("eth0")) {
                    continue;
                } else {
                    Enumeration<?> e2 = ni.getInetAddresses();
                    while (e2.hasMoreElements()) {
                        InetAddress ia = (InetAddress) e2.nextElement();
                        if (ia instanceof Inet6Address)
                            continue;
                        ip = ia.getHostAddress();
                    }
                    break;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        if(StringUtils.isBlank(ip)){
            if(address == null){
                try {
                    address = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
            if(address != null){
                ip = address.getHostAddress();
            }
        }
        return ip;
    }
}
