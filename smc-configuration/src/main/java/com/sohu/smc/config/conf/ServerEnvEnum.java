package com.sohu.smc.config.conf;

import org.apache.commons.lang.StringUtils;

/**
 * Created with IntelliJ IDEA.
 * User: xiaocaijing
 * Date: 13-5-29
 * Time: 下午5:30
 * 服务端运行的环境的枚举
 */
public enum ServerEnvEnum {
    /**
     * 线上环境
     */
    ONLINE("ONLINE"),
    /**
     * 开发环境
     */
    DEV("DEV"),
    /**
     * 测试环境
     */
    TEST("TEST"),
    /**
     * 预发布环境
     */
    PRE("PRE");

    public String code;

    ServerEnvEnum(String code) {
        this.code = code;
    }


    public static ServerEnvEnum getEnvByCode(String code) {
        ServerEnvEnum[] values = ServerEnvEnum.values();
        for (ServerEnvEnum each : values) {
            if (StringUtils.equals(each.code, code)) {
                return each;
            }
        }
        return ONLINE;
    }

}
