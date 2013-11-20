package com.sohu.smc.schedule.core.service;

import com.netflix.curator.x.discovery.ServiceInstance;
import com.smc.notify.Notify;
import com.sohu.smc.schedule.core.exception.DiscoveryException;
import com.sohu.smc.schedule.core.util.DiscoveryUtil;
import org.apache.commons.lang.StringUtils;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 10/17/13
 * Time: 5:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class SmcDiscovery {
    private static ServiceInstance<String> serviceInstance = null;
    private static String groupName = null;
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                DiscoveryUtil.unregist(serviceInstance);
            }
        });
    }
    public static void register(String groupName, int port) throws DiscoveryException {
        String address = Notify.getLocalIP();
        if(StringUtils.equals("address", "127.0.0.1") || StringUtils.equals("address", "localhost")){
            throw new DiscoveryException("This machine hasn't set ip address.Please set ip address first or invoke SmcDiscovery.register(groupName, address, port) method to complete register.");
        }
        Notify.init(port);
        ServiceInstance<String> instance = DiscoveryUtil.register(groupName, address, port, "SmcTask-" + groupName);
        if(instance == null){
            throw new DiscoveryException("[register]:Regist server instance to zookeeper error.");
        }else{
            serviceInstance = instance;
            SmcDiscovery.groupName = groupName;
        }
    }

    public static void register(String groupName, String address, int port) throws DiscoveryException {
        if(StringUtils.equals("address", "127.0.0.1") || StringUtils.equals("address", "localhost")){
            throw new DiscoveryException("The ip address " + address + " unusable.");
        }
        ServiceInstance<String> instance = DiscoveryUtil.register(groupName, address, port, "SmcTask-" + groupName);
        if(instance == null){
            throw new DiscoveryException("[register]:Regist server instance to zookeeper error.");
        }else{
            serviceInstance = instance;
            SmcDiscovery.groupName = groupName;
        }
    }

    public static ServiceInstance<String> getServiceInstance(){
        return serviceInstance;
    }

}
