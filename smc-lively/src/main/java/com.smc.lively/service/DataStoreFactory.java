package com.smc.lively.service;

import com.smc.lively.enums.LivelyItemEnum;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 2/25/14
 * Time: 4:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class DataStoreFactory {
    private static ConcurrentHashMap<LivelyItemEnum, DataStore> SERVICE_MAP = new ConcurrentHashMap<LivelyItemEnum, DataStore>();

    public static DataStore getInstance(LivelyItemEnum livelyItemEnum){
        DataStore dataStore = null;
        if(SERVICE_MAP.contains(livelyItemEnum.name)){
            dataStore = SERVICE_MAP.get(livelyItemEnum);
        } else{
            dataStore = new DataStore(livelyItemEnum.name);
            SERVICE_MAP.put(livelyItemEnum, dataStore);
        }
        return dataStore;
    }
}
