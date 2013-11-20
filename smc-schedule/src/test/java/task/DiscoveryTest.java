package task;

import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.CuratorFrameworkFactory;
import com.netflix.curator.retry.RetryOneTime;
import com.netflix.curator.x.discovery.*;
import com.sohu.smc.schedule.core.conf.SmcScheduleCoreConfig;
import com.sohu.smc.schedule.core.util.DiscoveryUtil;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 10/17/13
 * Time: 8:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class DiscoveryTest {

    public static void main(String[] args) throws Exception {


//        CuratorFramework client = CuratorFrameworkFactory.newClient(SmcScheduleCoreConfig.SCHEDULE_ZK_SERVER, 5 * 1000, 5 * 1000, new RetryOneTime(1));
//
//        client.start();
//
//        String basePath = "/discovery/test/";
//        final ServiceDiscovery discovery = ServiceDiscoveryBuilder.builder(String.class).client(client).basePath(basePath).build();
//        discovery.start();
//        ServiceCache<ServiceInstance> cache = discovery.serviceCacheBuilder().name("DiscoveryTest").build();
//        cache.start();
//
//        final ServiceInstance<String> instance1 = ServiceInstance.<String>builder().name("DiscoveryTest").address("192.168.1.100").port(8080).serviceType(ServiceType.DYNAMIC).payload("host1").build();
//        discovery.registerService(instance1);
//
////        Thread.sleep(5 * 1000);
//        final ServiceInstance<String> instance2 = ServiceInstance.<String>builder().name("DiscoveryTest").address("192.168.1.101").port(8080).serviceType(ServiceType.DYNAMIC).payload("host1").build();
//        discovery.registerService(instance2);
//
////        Thread.sleep(60 * 1000);
//        final ServiceInstance<String> instance3 = ServiceInstance.<String>builder().name("DiscoveryTest").address("192.168.1.102").port(8080).serviceType(ServiceType.DYNAMIC).payload("host1").build();
//        discovery.registerService(instance3);
//
////        Thread.sleep(3 * 1000);
//
//        Runtime.getRuntime().addShutdownHook(new Thread(){
//            @Override
//            public void run() {
//                try {
//                    System.out.println("shutdown!");
//                    discovery.unregisterService(instance1);
//                    discovery.unregisterService(instance2);
//                    discovery.unregisterService(instance3);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                super.run();
//            }
//        });
        while(true){
            List<ServiceInstance<String>> instance = DiscoveryUtil.getAllService("smcApi", true);
            System.out.println("======================================");
            for(ServiceInstance each : instance){
                System.out.println(each.getAddress());
            }
            System.out.println("======================================");

            Thread.sleep(5 * 1000);
        }

    }
}
