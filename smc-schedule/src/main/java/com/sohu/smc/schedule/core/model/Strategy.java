package com.sohu.smc.schedule.core.model;

import org.apache.commons.lang.StringUtils;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 10/18/13
 * Time: 6:41 PM
 * To change this template use File | Settings | File Templates.
 */
public enum Strategy {
    RANDOM("random", "随机"),
    POLLING("polling", "轮询"),
    STICKY("sticky", "固定");

    public String name;
    public String desc;

    Strategy(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public static Strategy getStrategyByName(String name) {
        if(StringUtils.isBlank(name)) return null;
        Strategy[] strategies = Strategy.values();
        for (Strategy each : strategies) {
            if (StringUtils.equals(each.name, name)) {
                return each;
            }
        }
        return null;
    }
}
