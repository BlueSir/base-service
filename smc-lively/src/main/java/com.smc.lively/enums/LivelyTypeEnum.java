package com.smc.lively.enums;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 2/25/14
 * Time: 7:41 PM
 * To change this template use File | Settings | File Templates.
 */
public enum LivelyTypeEnum {

    CLIENT_LIVELY(32, "Client相关的活跃用户池，分隔成16份"),
    PASSPORT_LIVELY(2, "Passport相关的活跃用户池，分隔为4份");

    int shared;
    String desc;
    LivelyTypeEnum(int shared, String desc){
        this.shared = shared;
        this.desc = desc;
    }

}
