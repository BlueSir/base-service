package task;

import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.CuratorFrameworkFactory;
import com.netflix.curator.retry.RetryOneTime;
import com.netflix.curator.x.discovery.*;
import com.sohu.smc.schedule.core.conf.SmcScheduleCoreConfig;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 10/17/13
 * Time: 8:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class DiscoveryTest3 {

    public static void main(String[] args) throws Exception {


        CuratorFramework client = CuratorFrameworkFactory.newClient(SmcScheduleCoreConfig.SCHEDULE_ZK_SERVER, new RetryOneTime(2));
        client.start();

        String basePath = "/discovery/test/";
        ServiceDiscovery discovery = ServiceDiscoveryBuilder.builder(String.class).client(client).basePath(basePath).build();
        discovery.start();
        ServiceCache<ServiceInstance> cache = discovery.serviceCacheBuilder().name("DiscoveryTest").build();
        cache.start();

        ServiceInstance<String> instance1 = ServiceInstance.<String>builder().name("DiscoveryTest").address("192.168.1.100").port(8080).serviceType(ServiceType.DYNAMIC).build();
        discovery.registerService(instance1);

        ServiceInstance<String> instance2 = ServiceInstance.<String>builder().name("DiscoveryTest").address("192.168.1.101").port(8080).serviceType(ServiceType.DYNAMIC).build();
        discovery.registerService(instance2);

        ServiceInstance<String> instance3 = ServiceInstance.<String>builder().name("DiscoveryTest").address("192.168.1.102").port(8080).serviceType(ServiceType.DYNAMIC).build();
        discovery.registerService(instance3);

        Thread.sleep(3 * 1000);


        List<ServiceInstance<ServiceInstance>> instance = cache.getInstances();
        for(ServiceInstance each : instance){
            System.out.println("================"+each.getAddress());

        discovery.unregisterService(instance1);
        }

        instance = cache.getInstances();
        for(ServiceInstance each : instance){
            System.out.println("================"+each.getAddress());
        }


        Thread.sleep(3 * 1000);

        instance = cache.getInstances();
        for(ServiceInstance each : instance){
            System.out.println("================"+each.getAddress());
        }
        Thread.sleep(5 * 60 * 1000);

    }
}
