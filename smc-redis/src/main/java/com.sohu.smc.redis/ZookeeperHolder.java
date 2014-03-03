package com.sohu.smc.redis;

import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.CuratorFrameworkFactory;
import com.netflix.curator.retry.RetryOneTime;

public class ZookeeperHolder {
    public static CuratorFramework zkClient = null;

    static {
        try {
            zkClient = CuratorFrameworkFactory.newClient("10.13.81.90:2181,10.13.81.74:2181,10.10.76.41:2181", new RetryOneTime(1));
            zkClient.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
