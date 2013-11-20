package task;


import com.sohu.smc.schedule.core.model.Schedule;
import com.sohu.smc.schedule.core.service.SchedulerService;
import com.sohu.smc.schedule.core.util.SchedulerHolder;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 10/24/13
 * Time: 8:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class SchedulerServiceTest {

    public static void main(String[] args) throws InterruptedException {
        try {
            SchedulerHolder.scheduler.start();

            List<Schedule> scheduleList = SchedulerService.getInstance().listSchedule();

            for(Schedule each : scheduleList){
                System.out.println(each);
            }

            Thread.sleep(1000);
            System.out.println("addSchedule:"+SchedulerService.getInstance().addSchedule("FetchWeibo", "SmcUser", "0/10 * * * * ?", "type", "strategy"));

            Thread.sleep(1000);
            System.out.println("addSchedule:"+SchedulerService.getInstance().addSchedule("FetchSina", "SmcUser", "0/5 * * * * ?", "type", "strategy"));

            scheduleList = SchedulerService.getInstance().listSchedule();

            for(Schedule each : scheduleList){
                System.out.println(each);
            }

            SchedulerService.getInstance().startSchedule("FetchWeibo","SmcUser");
            SchedulerService.getInstance().startSchedule("FetchSina","SmcUser");

            Thread.sleep(15000);

            SchedulerService.getInstance().pauseSchedule("FetchSina","SmcUser");

            Thread.sleep(30000);

            SchedulerService.getInstance().pauseSchedule("FetchWeibo","SmcUser");
            scheduleList = SchedulerService.getInstance().listSchedule();

            for(Schedule each : scheduleList){
                System.out.println(each);
            }

            SchedulerService.getInstance().removeSchedule("FetchSina","SmcUser");

            scheduleList = SchedulerService.getInstance().listSchedule();

            for(Schedule each : scheduleList){
                System.out.println(each);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        Thread.sleep(100000);
    }
}
