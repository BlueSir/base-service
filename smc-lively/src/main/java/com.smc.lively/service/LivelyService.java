package com.smc.lively.service;

import com.smc.lively.enums.LivelyItemEnum;
import com.sohu.smc.redis.SmcJedis;
import com.sohu.smc.redis.SmcJedisFactory;

import java.util.HashMap;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 2/25/14
 * Time: 2:34 PM
 */
public class LivelyService {
    static SmcJedis jedis = SmcJedisFactory.getInstance("lively");
    static HashMap<LivelyItemEnum, Set<Long>> LIVELY_CACHE = new HashMap<LivelyItemEnum, Set<Long>>();
    private static LivelyService INSTANCE = new LivelyService();
    private LivelyService(){}

    public static void init(LivelyItemEnum livelyItemEnum){
        DataStore dataStore = DataStoreFactory.getInstance(livelyItemEnum);
        if(dataStore.isTableExsist()){
            Set<Long> livelyFromBD = dataStore.queryAll();
            LIVELY_CACHE.put(livelyItemEnum, livelyFromBD);
        }else{
            LivelyRedis.getAllLively(livelyItemEnum);
        }

    }
    public static LivelyService getInstance(){
        return INSTANCE;
    }

    public void loadLocalData(){

    }

    public Set<Long> getAllLively(LivelyItemEnum livelyItemEnum){
        return LIVELY_CACHE.get(livelyItemEnum);
    }

    public boolean isLively(LivelyItemEnum livelyItemEnum, long item){
        Set<Long> lively = LIVELY_CACHE.get(livelyItemEnum);
        if(lively == null){
            return false;
        }else{
            return lively.contains(item);
        }
    }


}
