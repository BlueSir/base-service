package com.sohu.smc.redis;

import java.util.Hashtable;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 11/30/13
 * Time: 5:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class SmcJedisFactory {
    private static final Hashtable<String, SmcJedis> INSTANCE_LIST = new Hashtable<String, SmcJedis>();

    public static SmcJedis getInstance(String pool){
        if(INSTANCE_LIST.containsKey(pool)){
            return INSTANCE_LIST.get(pool);
        } else {
            SmcJedis instance = new SmcJedis(pool);
            INSTANCE_LIST.put(pool, instance);
            return instance;
        }
    }
}
