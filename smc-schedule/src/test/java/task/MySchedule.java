package task;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 10/23/13
 * Time: 6:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class MySchedule implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobKey jobKey = context.getJobDetail().getKey();
        System.out.println("SimpleJob says: " + jobKey + " executing at " + new Date());
    }
}
