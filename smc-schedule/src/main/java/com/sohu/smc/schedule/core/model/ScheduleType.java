package com.sohu.smc.schedule.core.model;

import org.apache.commons.lang.StringUtils;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 10/23/13
 * Time: 2:47 PM
 * To change this template use File | Settings | File Templates.
 */
public enum ScheduleType {
    SINGLE("single"),
    ALL("all");

    public String name;
    ScheduleType(String name){
        this.name = name;
    }

    public static ScheduleType getScheduleTypeByName(String name){
        if(StringUtils.isBlank(name)) return null;
        ScheduleType[] scheduleTypes = ScheduleType.values();
        for(ScheduleType each : scheduleTypes){
            if(StringUtils.equals(each.name, name)){
                return each;
            }
        }
        return null;
    }
}
