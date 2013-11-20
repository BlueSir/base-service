package com.sohu.smc.schedule.core.service;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 10/22/13
 * Time: 4:09 PM
 */
public interface ScheduleListener {
    public boolean executeTask(String message) throws Exception;
}
