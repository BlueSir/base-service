package com.smc.lively.enums;

import org.apache.commons.lang.time.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 2/25/14
 * Time: 3:54 PM
 * To change this template use File | Settings | File Templates.
 */
public enum LivelyItemEnum {

    PID_1_DAY("pid_1_day", 1, LivelyTypeEnum.PASSPORT_LIVELY, "一天内的活跃用户passportId"),
    PID_3_DAY("pid_3_day", 3, LivelyTypeEnum.PASSPORT_LIVELY, "三天内的活跃用户passportId"),
    CID_1_DAY("cid_1_day", 1, LivelyTypeEnum.CLIENT_LIVELY, "一天内的活跃用户cid"),
    CID_3_DAY("cid_3_day", 3, LivelyTypeEnum.CLIENT_LIVELY,"三天内的活跃用户cid");
    public String name;
    public int days;
    public String desc;
    public LivelyTypeEnum type;
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
    LivelyItemEnum(String name, int days, LivelyTypeEnum type, String desc){
        this.name = name;
        this.days = days;
        this.desc = desc;
        this.type = type;
    }

    public long getMinScore(){
        return Long.valueOf(sdf.format(DateUtils.addDays(new Date(), this.days * (-1))));
    }

    public long getMaxScore(){
        return Long.valueOf(sdf.format(DateUtils.addHours(new Date(), 1)));
    }

    public static void main(String[] args){
        System.out.println(LivelyItemEnum.CID_1_DAY.getMaxScore() + "|" + LivelyItemEnum.CID_1_DAY.getMinScore());
    }
}
