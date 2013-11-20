package task;

import com.sohu.smc.schedule.core.exception.DiscoveryException;
import com.sohu.smc.schedule.core.service.ScheduleListener;
import com.sohu.smc.schedule.core.service.SmcDiscovery;
import com.sohu.smc.schedule.core.service.SmcSchedule;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 10/30/13
 * Time: 4:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConsumerTest2 {

    public static void main(String[] args) throws DiscoveryException {
        SmcDiscovery.register("smcApi", 8080);
        SmcSchedule.execute("smc_api_schedule",new ScheduleListener() {
            @Override
            public boolean executeTask(String message) throws Exception {
                System.out.println("smc_api_schedule execute");
                return true;
            }
        });
    }
}
