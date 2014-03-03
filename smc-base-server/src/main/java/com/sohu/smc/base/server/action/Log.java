package com.sohu.smc.base.server.action;

import com.sohu.smc.redis.SmcJedis;
import com.sohu.smc.redis.SmcJedisFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 1/3/14
 * Time: 5:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class Log {
    static SmcJedis logRedis = SmcJedisFactory.getInstance("counter");

    public static void log(String key, String message){
        try{

            logRedis.lpush("log_"+ key , message);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static List<String> getLog(String key){
        try{
            return logRedis.lrange("log_"+key, 0 , -1);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(df.format(new Date()));
    }
}
