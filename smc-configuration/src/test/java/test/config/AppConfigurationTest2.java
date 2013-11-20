package test.config;

import com.netflix.config.DynamicStringProperty;
import com.netflix.curator.CuratorZookeeperClient;
import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.CuratorFrameworkFactory;
import com.netflix.curator.retry.ExponentialBackoffRetry;
import com.sohu.smc.config.model.AppConfiguration;
import junit.framework.TestCase;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 11/12/13
 * Time: 10:28 AM
 * To change this template use File | Settings | File Templates.
 */
public class AppConfigurationTest2 extends TestCase{

    //从配置中心取zookeeper的服务器列表
    static DynamicStringProperty zkServer = AppConfiguration.getString("smc.zookeeper.server", "",

        //如果从配置中心后台修改了zookeeper的服务器列表，则Runnable的run方法会被执行，重新实例化zookeeper.
        new Runnable() {
            @Override
            public void run() {
            init();
        }
    });

    //zookeeper的实例
    static CuratorFramework zkClient = null;

    //静态代码块初始化zookeeper实例
    static{
        init();
    }


    public static void init(){
        //如果zookeeper已经被实例化过，则先关闭之前的连接
        if(zkClient != null) zkClient.close();

        //实例化zookeeper
        zkClient = CuratorFrameworkFactory.newClient(zkServer.get(), new ExponentialBackoffRetry(1000, 3));

    }
}
