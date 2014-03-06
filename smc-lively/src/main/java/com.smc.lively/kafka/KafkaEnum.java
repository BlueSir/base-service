package com.smc.lively.kafka;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 3/5/14
 * Time: 11:13 AM
 * To change this template use File | Settings | File Templates.
 */
public enum KafkaEnum {
    LIVELY_ADD_TOPIC("lively_add"),
    LIVELY_DEL_TOPIC("lively_del");

    public String name;
    KafkaEnum(String name){
        this.name = name;
    }

}
