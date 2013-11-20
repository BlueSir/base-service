package task;

import com.netflix.curator.x.discovery.ServiceInstance;
import com.sohu.smc.schedule.core.model.Strategy;
import com.sohu.smc.schedule.core.util.DiscoveryUtil;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 10/21/13
 * Time: 11:16 AM
 * To change this template use File | Settings | File Templates.
 */
public class TaskTest {

    public static void main(String args[]) throws InterruptedException {
        String path = "/discovery/test/task";
        DiscoveryUtil.register("TaskTest", "192.168.1.101", 8080, "server1");


        while(true){
            List<ServiceInstance<String>> hosts = DiscoveryUtil.getAllService("TaskTest", true);
            ServiceInstance<String> polling = DiscoveryUtil.getService("TaskTest", Strategy.POLLING);
            System.out.println("## polling: host="+polling.getAddress()+", port="+polling.getPort());

            ServiceInstance<String> sticky = DiscoveryUtil.getService("TaskTest", Strategy.STICKY);
            System.out.println("## sticky: host="+ sticky.getAddress()+", port="+sticky.getPort());

            for(ServiceInstance<String> each : hosts){
                System.out.println("## host="+each.getAddress()+",port="+each.getPort());
            }
            System.out.println("==========================================");
            Thread.sleep(10000);
        }



    }
}
