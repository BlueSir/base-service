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
public class ConsumerTest {

    public static void main(String[] args){
        try {
            SmcDiscovery.register("smcApi", 8070);
        } catch (DiscoveryException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        SmcSchedule.execute("smc_api_schedule",new ScheduleListener() {
            @Override
            public boolean executeTask(String message) throws Exception {
                System.out.println("smc_api_schedule execute");
                return true;
            }
        });
    }
}
