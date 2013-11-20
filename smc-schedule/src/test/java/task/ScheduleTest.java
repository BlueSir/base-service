package task;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.*;
import java.text.ParseException;

import static org.quartz.TriggerBuilder.*;
import static org.quartz.JobBuilder.*;
import static org.quartz.CronScheduleBuilder.*;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 10/23/13
 * Time: 3:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class ScheduleTest {

    public static void main(String[] args) throws ParseException, SchedulerException, InterruptedException {
        SchedulerFactory sf = new StdSchedulerFactory();

        Collection<Scheduler> schedulerList = sf.getAllSchedulers();
        for(Scheduler each : schedulerList){
            System.out.println("================"+each.getSchedulerName());
        }
        Scheduler sched = sf.getScheduler();

//        JobDetail jobDetail = sched.getJobDetail(new JobKey("job1","group1"));
//        System.out.println("DESC="+jobDetail.getDescription());
//        System.out.println("================"+sched.getSchedulerName());
        JobDetail job = newJob(MySchedule.class)
                .withIdentity("job1", "group1").usingJobData("key","value").withDescription("STRAGY:TYPE").storeDurably()
                .build();
        sched.addJob(job, true);


//
        CronTrigger trigger = newTrigger()
                .withIdentity("trigger1", "group1")
                .withSchedule(cronSchedule("0/10 * * * * ?"))
                .build();
//        Set<Trigger> triggerSet = new HashSet<Trigger>();
//        triggerSet.add(trigger);
//        sched.rescheduleJob(trigger.getKey(), trigger);
//        System.out.println(trigger.getKey());
            sched.scheduleJob(job, trigger);
////        sched.unscheduleJob(trigger.getKey());
//        sched.start();

        Thread.sleep(90L * 1000L);
        sched.shutdown(true);
    }
}