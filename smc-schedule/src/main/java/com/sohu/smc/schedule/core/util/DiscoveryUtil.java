package com.sohu.smc.schedule.core.util;

import com.google.common.collect.Maps;
import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.CuratorFrameworkFactory;
import com.netflix.curator.framework.api.CuratorEvent;
import com.netflix.curator.framework.api.CuratorListener;
import com.netflix.curator.framework.state.ConnectionState;
import com.netflix.curator.framework.state.ConnectionStateListener;
import com.netflix.curator.retry.RetryNTimes;
import com.netflix.curator.x.discovery.*;
import com.netflix.curator.x.discovery.details.JsonInstanceSerializer;
import com.netflix.curator.x.discovery.details.ServiceCacheListener;
import com.smc.local.cache.TimedCache;
import com.sohu.smc.schedule.core.conf.SmcScheduleCoreConfig;
import com.sohu.smc.schedule.core.model.Strategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class DiscoveryUtil {

    private static final Logger log = LoggerFactory.getLogger(DiscoveryUtil.class.getName());
    public static ServiceDiscovery<String> serviceDiscovery;
    public static CuratorFramework client = null;
    public static final String path = "/discovery/instance/";
    public static Map<String, ServiceCache<String>> serviceCacheMap = Maps.newConcurrentMap();

    public static TimedCache<String, List<ServiceInstance<String>>> serviceInstanceCache = new TimedCache<String, List<ServiceInstance<String>>>(1000, 30 * 60 * 1000);

    static {
        buildConnection();
    }

    private static synchronized void buildConnection() {
        try {
            client = CuratorFrameworkFactory.builder().connectString(SmcScheduleCoreConfig.SCHEDULE_ZK_SERVER).retryPolicy(new RetryNTimes(3, 20)).sessionTimeoutMs(3000).build();
            client.start();

            client.getCuratorListenable().addListener(new CuratorListener(){

                @Override
                public void eventReceived(CuratorFramework client, CuratorEvent event) throws Exception {
                    switch (event.getType()) {
                        case DELETE:
                        case CREATE:
                        case SET_DATA:
                            System.out.println(event.getName());
                            log.warn("service stat change:" + event.getName() + "|path:" + event.getPath());
                            client.sync(event.getPath(), event.getContext());       //sync the server
                            break;

                        case CLOSING:
                            client.getConnectionStateListenable();
                            break;

                        default:
                            break;
                    }
                }
            });
            client.getConnectionStateListenable().addListener(new ConnectionStateListener() {

                @Override
                public void stateChanged(CuratorFramework client, ConnectionState newState) {
                    switch (newState) {
                        case LOST:
                            //reconnect
                            rebuildConnection();
                            log.warn("zk client connection lost, reconnect it.");
                            break;
                        default:
                            break;
                    }
                }
            });
            serviceDiscovery = ServiceDiscoveryBuilder.builder(String.class).basePath(path).serializer(new JsonInstanceSerializer<String>(String.class)).client(client).build();
            serviceDiscovery.start();

            log.info("build a new zk connection:" + client.toString());

        } catch (IOException e) {
            log.error("create connection err,", e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized void rebuildConnection() {
        buildConnection();
    }

    public static ServiceInstance<String> register(final String groupName, String address, int port, String description) {
        ServiceInstance<String> instance = null;
        try {
//            Collection<ServiceInstance<String>> instances = getAllService(groupName, false);
//            if (instances != null && instances.size() > 0) {
//                for (ServiceInstance each : instances) {
//                    if (StringUtils.equals(groupName, each.getName()) && StringUtils.equals(address, each.getAddress()) && port == each.getPort().intValue()) {
//                        log.warn("register service falure, the service[" + groupName + "-" + port + "] has existed.");
//                        return each;
//                    }
//
//                }
//            }
            instance = ServiceInstance.<String>builder().address(address).port(port).payload(description).name(groupName).serviceType(ServiceType.DYNAMIC).build();
            serviceDiscovery.registerService(instance);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return instance;
    }

    public static void unregist( ServiceInstance<String> instance) {
        try {
            serviceDiscovery.unregisterService(instance);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static ServiceInstance<String> getService(final String serviceName, Strategy strategy) {
        ServiceInstance<String> serviceInstance = null;

        try {
            List<ServiceInstance<String>> allInstance = getAllService(serviceName, true);
            switch (strategy) {
                case RANDOM: {
                    return getRandomService(allInstance);
                }
                case POLLING: {
                    return getPollingServie(allInstance);
                }
                case STICKY: {
                    return getStickyService(allInstance);
                }
            }
        } catch (Exception e) {
            log.error("", e);
        }

        return serviceInstance;
    }

    private static final AtomicReference<ServiceInstance<String>> ourInstance = new AtomicReference<ServiceInstance<String>>(null);
    private static final AtomicInteger                         instanceNumber = new AtomicInteger(-1);

    private static ServiceInstance<String> getStickyService(List<ServiceInstance<String>> allInstance) {
        {
            ServiceInstance<String>                localOurInstance = ourInstance.get();
            if ( !allInstance.contains(localOurInstance) )
            {
                ourInstance.compareAndSet(localOurInstance, null);
            }
        }

        if ( ourInstance.get() == null )
        {
            ServiceInstance<String> instance = getRandomService(allInstance);
            if ( ourInstance.compareAndSet(null, instance) )
            {
                instanceNumber.incrementAndGet();
            }
        }
        return ourInstance.get();
    }

    public static List<ServiceInstance<String>> getAllService(final String groupName, boolean needCache) {

        List<ServiceInstance<String>> instances = null;
        try {
            if(!serviceCacheMap.containsKey(groupName)){
                ServiceCache<String> serviceCache = serviceDiscovery.serviceCacheBuilder().name(groupName).build();
                serviceCacheMap.put(groupName, serviceCache);
                serviceCache.addListener(new ServiceCacheListener() {
                    @Override
                    public void cacheChanged() {
                        System.out.println("[ServiceCacheListener.cacheChanged]:groupName=" + groupName);
                        getAllService(groupName, false);
                    }

                    @Override
                    public void stateChanged(CuratorFramework client, ConnectionState newState) {
                        log.info("stateChanged:" + newState.name());
                    }
                });
                serviceCache.start();
            }
            if (needCache) {
                instances = serviceInstanceCache.get(groupName);
                if (instances != null) {
                    return instances;
                }
            }

            log.info("queryForInstances from zookeeper. groupName=" + groupName);
            System.out.println("queryForInstances from zookeeper. groupName=" + groupName);
            instances = (List<ServiceInstance<String>>) serviceDiscovery.queryForInstances(groupName);
            if (instances == null) {
                instances = new ArrayList<ServiceInstance<String>>();
            }
            serviceInstanceCache.put(groupName, instances);
        } catch (Exception e) {
            log.error("", e);
        }

        return instances;
    }

    private static final AtomicInteger index = new AtomicInteger(0);

    private static ServiceInstance<String> getPollingServie(List<ServiceInstance<String>> allInstance) {
        if (allInstance == null || allInstance.size() == 0) {
            return null;
        }
        int thisIndex = Math.abs(index.getAndIncrement());
        return allInstance.get(thisIndex % allInstance.size());
    }


    private static final Random random = new Random();

    private static ServiceInstance<String> getRandomService(List<ServiceInstance<String>> allInstance) {
        if (allInstance == null || allInstance.size() == 0) {
            return null;
        }
        int index = random.nextInt(allInstance.size());
        return allInstance.get(index);
    }

}
