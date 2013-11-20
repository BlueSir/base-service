package task;

import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.CuratorFrameworkFactory;
import com.netflix.curator.retry.RetryOneTime;
import com.netflix.curator.x.discovery.ServiceCache;
import com.netflix.curator.x.discovery.ServiceDiscovery;
import com.netflix.curator.x.discovery.ServiceDiscoveryBuilder;
import com.netflix.curator.x.discovery.ServiceInstance;
import com.sohu.smc.schedule.core.conf.SmcScheduleCoreConfig;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 10/17/13
 * Time: 8:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class DiscoveryTest2 {

    static DateFormat df = new SimpleDateFormat("HH:mm:ss");
    public static void main(String[] args) throws Exception {


        CuratorFramework client = CuratorFrameworkFactory.newClient(SmcScheduleCoreConfig.SCHEDULE_ZK_SERVER, new RetryOneTime(2));
        client.start();

        String basePath = "/discovery/test/";
        ServiceDiscovery discovery = ServiceDiscoveryBuilder.builder(String.class).client(client).basePath(basePath).build();
        discovery.start();
        ServiceCache<ServiceInstance> cache = discovery.serviceCacheBuilder().name("DiscoveryTest").build();
        cache.start();

        while(true){
            Thread.sleep(1000);
            Collection<ServiceInstance> instance = discovery.queryForInstances("DiscoveryTest");
            System.out.println("====================　「"+ df.format(new Date()) + "」　=======================");
            for(ServiceInstance each : instance){
                System.out.println("================"+each.getAddress());
            }
        }
    }
}
