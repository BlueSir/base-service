package task;

import org.quartz.CronExpression;

import java.text.ParseException;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 10/24/13
 * Time: 8:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class CronExpressionTest {

    public static void main(String[] args) throws ParseException {
        CronExpression cron = new CronExpression("0/10 * * * * ?");
        System.out.println(cron.toString());
        System.out.println(cron.getCronExpression());
        System.out.println(cron.getExpressionSummary());
        System.out.println(cron.getFinalFireTime());
        System.out.println(cron.getTimeZone());
    }
}
