package task;

import com.sohu.smc.schedule.core.exception.DiscoveryException;
import com.sohu.smc.schedule.core.service.ScheduleListener;
import com.sohu.smc.schedule.core.service.SmcDiscovery;
import com.sohu.smc.schedule.core.service.SmcSchedule;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 11/14/13
 * Time: 10:09 AM
 * To change this template use File | Settings | File Templates.
 */
public class TaskListenerTest {
    public static void main(String[] args) throws InterruptedException, DiscoveryException {
       SmcDiscovery.register("RestLiUser","192.168.1.100", 8070);

//        Thread.sleep(30 * 1000);
//        SmcDiscovery.register("smcApi","192.168.1.101", 8070);
//
//        Thread.sleep(30 * 1000);
//        SmcDiscovery.register("smcApi","192.168.1.102", 8070);
//
//        Thread.sleep(30 * 1000);
//        SmcDiscovery.register("smcApi","192.168.1.103", 8070);
        SmcSchedule.execute("smc_api_schedule", new ScheduleListener() {
            @Override
            public boolean executeTask(String message) throws Exception {
                System.out.println("=====================================");
                return true;
            }
        });
    }
}
