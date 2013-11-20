package com.nsq;

import org.apache.commons.lang.StringUtils;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 10/23/13
 * Time: 11:35 AM
 * To change this template use File | Settings | File Templates.
 */
public enum MessageType {
    /**
     * 删除本地缓存
     */
    LOCAL_CAHCE_DELETE("LD"),

    /**
     * 刷新本地群组缓存
     */
    LOCAL_CACHE_REFRUSH_GROUP("LR"),

    /**
     * 任务计划：单台服务器执行
     */
    SCHEDULE_SINGLE_EXECUTE("SS"),

    /**
     * 任务计划：单台服务器执行
     */
    SCHEDULE_ALL_EXECUTE("SA");

    public String code;
    MessageType(String code){
        this.code = code;
    }

    public static MessageType getMessageTypeByCode(String code){
        MessageType[] messageTypes = MessageType.values();
        for(MessageType each : messageTypes){
            if(StringUtils.equals(each.code, code)){
                return each;
            }
        }
        System.err.println("[MessageType.getMessageTypeByCode]:Code non-existent.code="+code);
        return null;
    }
}
