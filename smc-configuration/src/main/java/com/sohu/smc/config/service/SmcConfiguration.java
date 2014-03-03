package com.sohu.smc.config.service;

import com.netflix.config.*;
import com.netflix.config.source.ZooKeeperConfigurationSource;
import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.CuratorFrameworkFactory;
import com.netflix.curator.retry.ExponentialBackoffRetry;
import com.netflix.curator.utils.DebugUtils;
import com.sohu.smc.config.conf.ServerEnvEnum;
import com.sohu.smc.config.exception.SmcConfigurationException;
import org.apache.commons.lang.StringUtils;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 11/7/13
 * Time: 12:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class SmcConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(SmcConfiguration.class);

    private static String CONFIG_ROOT_PATH = "/smc/config/%s";
    private static CuratorFramework client;
    public static ZooKeeperConfigurationSource zkConfigSource;
    private static final Charset charset = Charset.forName("UTF-8");
    private static AtomicBoolean isInited = new AtomicBoolean(false);
    public static ServerEnvEnum environment = null;
    public static Map<String,String> overridePropertyMap = new HashMap<String,String>();
    static Map<String,Map<String, Map<String, Object>>> allProperties = null;

    /**
     * 这个只用在开发和测试环境，用来对配置中心的值进行覆盖，方便调试，map中的key是配置中心的key，value是要覆盖的值。
     * @return
     */
    public static void setOverriedProperty(String key, String value) {
        overridePropertyMap.put(key, value);
    }
    public static void init() {
        String env = null;
        if (isInited.compareAndSet(false, true)) {
            System.setProperty(DebugUtils.PROPERTY_DONT_LOG_CONNECTION_ISSUES, "true");
            System.setProperty("archaius.deployment.applicationId", "smc-configuration");
            env = ConfigurationManager.getDeploymentContext().getDeploymentEnvironment();
            if (StringUtils.isBlank(env)) {
                env = "dev";
            }
            environment = ServerEnvEnum.getEnvByCode(env);
            if(environment == null){
                logger.error("[smc-configuration]:Invalid environment:" + env + ",environment must contains:[online,dev,test,pre]");
                System.exit(0);
            }
            CONFIG_ROOT_PATH = String.format(CONFIG_ROOT_PATH, env);
            try {
                ConfigurationManager.loadCascadedPropertiesFromResources("smc-configuration");
            } catch (IOException e) {
                e.printStackTrace();
            }
            String[] zkServers = ConfigurationManager.getConfigInstance().getStringArray("smc.configuration.zk.server");
            String zkServer = "";
            if (zkServers.length == 1) {
                zkServer = zkServers[0];
            } else if (zkServers.length > 1) {
                StringBuilder sb = new StringBuilder();
                for (String each : zkServers) {
                    sb.append(each).append(",");
                }
                zkServer = sb.substring(0, sb.length() - 1);
            }
            logger.info("[smc-configuration]:smc.configuration.zk.server="+zkServer);
            client = CuratorFrameworkFactory.newClient(zkServer, new ExponentialBackoffRetry(1000, 3));
            client.start();

            zkConfigSource = new ZooKeeperConfigurationSource(client, CONFIG_ROOT_PATH);
            try {
                zkConfigSource.start();
            } catch (Exception e) {
                logger.error("SmcConfiguration init error.Zookeeper init error.");
                e.printStackTrace();
            }

            final DynamicWatchedConfiguration zkDynamicOverrideConfig = new DynamicWatchedConfiguration(zkConfigSource);

            final ConcurrentCompositeConfiguration compositeConfig = new ConcurrentCompositeConfiguration();
            compositeConfig.addConfiguration(zkDynamicOverrideConfig, "zk dynamic override configuration");
            ConfigurationManager.install(compositeConfig);

            //设置配置项到system property，用来对Spring的配置文件进行占位的替换。
            applyPlaceHolder();
        }
    }

    static void applyPlaceHolder(){
        try {
            Map<String, Object> data = zkConfigSource.getCurrentData();
            if(data != null){
                Iterator<String> iterator = data.keySet().iterator();
                while(iterator.hasNext()){
                    String key = iterator.next();
                    Object value = data.get(key);
                    System.setProperty(key, value.toString());
                }
            }
        } catch (Exception e) {
            logger.error("SmcConfiguration init error.Set configuration to system property");
            e.printStackTrace();
        }
    }

    public static boolean setProperty(String key, String value) throws SmcConfigurationException {
        final String path = CONFIG_ROOT_PATH + "/" + key;

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        zkConfigSource.addUpdateListener(new WatchedUpdateListener() {
            @Override
            public void updateConfiguration(WatchedUpdateResult result) {
                allProperties = null;
                countDownLatch.countDown();
            }
        });
        byte[] data = value.getBytes(charset);

        try {
            // attempt to create (intentionally doing this instead of checkExists())
            client.create().creatingParentsIfNeeded().forPath(path, data);
            client.getZookeeperClient().getZooKeeper().exists("/", new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                }
            });

            return true;
        } catch (KeeperException.NodeExistsException exc) {
            // key already exists - update the data instead
            try {
                client.setData().forPath(path, data);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                throw new SmcConfigurationException("设置配置项失败，错误原因："+ e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            countDownLatch.await();
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new SmcConfigurationException("设置配置项失败，错误原因："+ e.getMessage());
        }
    }

    public static boolean checkExists(String key){
        String value = DynamicPropertyFactory.getInstance().getStringProperty(key, null).get();

        if(StringUtils.isNotBlank(value)){
            return true;
        }
        return false;
    }

    public static Map<String,Map<String, Map<String, Object>>> properties() throws SmcConfigurationException {

        if(allProperties != null){
            return allProperties;
        }
        allProperties = new TreeMap<String, Map<String, Map<String, Object>>>();
        try {
            Map<String, Object> data = zkConfigSource.getCurrentData();

            if(data != null){
                Iterator<String> it = data.keySet().iterator();
                while(it.hasNext()){
                    String key = it.next();
                    String prefix = getPrefix(key);
                    String subPrefix = getSubPrefix(key);
                    if(allProperties.containsKey(prefix)){
                        Map<String, Map<String, Object>> properties = allProperties.get(prefix);
                        if(properties.containsKey(subPrefix)){
                            Map<String, Object> subProperties = properties.get(subPrefix);
                            subProperties.put(key, data.get(key));
                            properties.put(subPrefix, subProperties);
                        } else{
                            Map<String, Object> subProperties = new TreeMap<String, Object>();
                            subProperties.put(key, data.get(key));
                            properties.put(subPrefix, subProperties);
                        }

                    } else {
                        Map<String, Map<String, Object>> properties = new TreeMap<String, Map<String, Object>>();
                        Map<String, Object> subProperties = new TreeMap<String, Object>();
                        subProperties.put(key, data.get(key));
                        properties.put(subPrefix, subProperties);
                        allProperties.put(prefix, properties);
                    }

                }
            }
        } catch (Exception e) {
            throw new SmcConfigurationException("获取全部配置项失败，错误原因："+ e.getMessage());
        }
        return allProperties;

    }

    public static Map<String, Object> getPropertiesBySubPrefix(String subPrefix) throws SmcConfigurationException {
        allProperties = properties();
        String prefix = getPrefix(subPrefix);
        if(allProperties.containsKey(prefix)){
            Map<String, Map<String, Object>> pro = allProperties.get(prefix);
            return pro.get(subPrefix);
        }
        return null;
    }

    public static boolean remove(String key) throws SmcConfigurationException {
        final String path = CONFIG_ROOT_PATH + "/" + key;
        try {
            client.delete().forPath(path);
            allProperties = null;
            return true;
        } catch (Exception e) {
            throw new SmcConfigurationException("删除配置项失败，错误原因："+ e.getMessage());
        }
    }

    private static String getPrefix(String key){
        String[] strs = StringUtils.split(key, ".");
        if(strs.length <= 2){
            return key;
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(strs[0]).append(".").append(strs[1]);
            return sb.toString();
        }
    }

    private static String getSubPrefix(String key){
        String[] strs = StringUtils.split(key, ".");
        if(strs.length <= 3){
            return key;
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(strs[0]).append(".").append(strs[1]).append(".").append(strs[2]);
            return sb.toString();
        }
    }

    public static void main(String[] args) throws SmcConfigurationException {
        SmcConfiguration.init();
        try {
            setProperty("smc.configuration.test1", "value1");
            setProperty("smc.configuration.test2", "value2");
            setProperty("smc.configuration.test3", "value3");
        } catch (SmcConfigurationException e) {
            e.printStackTrace();
        }
        Map<String, Map<String, Map<String, Object>>> properties = SmcConfiguration.properties();
        Iterator<String> prefixIt = properties.keySet().iterator();
        while(prefixIt.hasNext()){
            String prefix = prefixIt.next();

            System.out.println("------------------" + prefix +"------------------------");
            Map<String, Map<String, Object>> values = properties.get(prefix);
            Iterator<String> valuesIt = values.keySet().iterator();
            while(valuesIt.hasNext()){
                String subPrefix = valuesIt.next();
                Map<String, Object> proMap = values.get(subPrefix);
                System.out.println("=============== " + subPrefix + " ==================");
                Iterator<String> proIt = proMap.keySet().iterator();
                while(proIt.hasNext()){
                    String proKey = proIt.next();
                    Object proValue = proMap.get(proKey);
                    System.out.println(proKey + " | "+ proValue);
                }
            }
        }

        Map<String, Object> map = getPropertiesBySubPrefix("smc.redis.counter");
        Iterator<String> it = map.keySet().iterator();
        while(it.hasNext()){
            String key = it.next();
            System.out.println("key="+key+",value="+map.get(key));
        }
    }

}
