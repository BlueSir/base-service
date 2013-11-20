package com.sohu.smc.schedule.core.util;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 10/24/13
 * Time: 7:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class SchedulerHolder {

    public static SchedulerFactory factory = new StdSchedulerFactory();
    public static Scheduler scheduler = null;
    static{
        try {
            scheduler = factory.getScheduler();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
