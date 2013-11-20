package task;

import com.smc.notify.Notify;
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
public class ConsumerTest3 {

    public static void main(String[] args) throws DiscoveryException {
        Notify.init(8070);
        SmcDiscovery.register("RestLiUser",8090);
        SmcSchedule.execute("smc.api.task",new ScheduleListener() {
            @Override
            public boolean executeTask(String message) throws Exception {
                System.out.println("smc.api.task execute");
                return true;
            }
        });
    }
}
