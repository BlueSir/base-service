package com.sohu.smc.config.service;

import com.netflix.config.*;
import com.netflix.config.source.ZooKeeperConfigurationSource;
import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.CuratorFrameworkFactory;
import com.netflix.curator.retry.ExponentialBackoffRetry;
import com.netflix.curator.utils.DebugUtils;
import com.sohu.smc.config.exception.SmcConfigurationException;
import org.apache.commons.lang.StringUtils;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
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

    public static void init() {
        if (isInited.compareAndSet(false, true)) {
            System.setProperty(DebugUtils.PROPERTY_DONT_LOG_CONNECTION_ISSUES, "true");
            System.setProperty("archaius.deployment.applicationId", "smc-configuration");
            String env = ConfigurationManager.getDeploymentContext().getDeploymentEnvironment();
            if (StringUtils.isBlank(env)) {
                env = "dev";
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

        }
    }

    public static void applyPlaceHolder(){
        init();
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
            e.printStackTrace();
        }
    }

    public static boolean setProperty(String key, String value) throws SmcConfigurationException {
        final String path = CONFIG_ROOT_PATH + "/" + key;

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        zkConfigSource.addUpdateListener(new WatchedUpdateListener() {
            @Override
            public void updateConfiguration(WatchedUpdateResult result) {
                countDownLatch.countDown();
            }
        });
        byte[] data = value.getBytes(charset);

        try {
            // attempt to create (intentionally doing this instead of checkExists())
            client.create().creatingParentsIfNeeded().forPath(path, data);
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

    public static Map<String,Map<String, Object>> properties() throws SmcConfigurationException {
        TreeMap<String, Map<String, Object>> treeMap = new TreeMap<String, Map<String, Object>>();
        try {
            Map<String, Object> data = zkConfigSource.getCurrentData();

            if(data != null){
                Iterator<String> it = data.keySet().iterator();
                while(it.hasNext()){
                    String key = it.next();
                    String prefix = getPrefix(key);
                    if(treeMap.containsKey(prefix)){
                        Map<String, Object> properties = treeMap.get(prefix);
                        properties.put(key, data.get(key));
                    } else {
                        Map<String, Object> properties = new TreeMap<String, Object>();
                        properties.put(key, data.get(key));
                        treeMap.put(prefix, properties);
                    }

                }
            }
        } catch (Exception e) {
            throw new SmcConfigurationException("获取全部配置项失败，错误原因："+ e.getMessage());
        }
        return treeMap;

    }

    public static boolean remove(String key) throws SmcConfigurationException {
        final String path = CONFIG_ROOT_PATH + "/" + key;
        try {
            client.delete().forPath(path);
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

    public static void main(String[] args) throws SmcConfigurationException {
        SmcConfiguration.init();
        try {
            setProperty("smc.configuration.test1", "value1");
            setProperty("smc.configuration.test2", "value2");
            setProperty("smc.configuration.test3", "value3");
        } catch (SmcConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        Map<String, Map<String,Object>> properties = SmcConfiguration.properties();
        Iterator<String> prefixIt = properties.keySet().iterator();
        while(prefixIt.hasNext()){
            String prefix = prefixIt.next();

            Map<String, Object> values = properties.get(prefix);
            Iterator<String> valuesIt = values.keySet().iterator();
            while(valuesIt.hasNext()){
                String key = valuesIt.next();
                Object value = values.get(key);
                System.out.println(key + " | "+ value);
            }
        }
    }

}
