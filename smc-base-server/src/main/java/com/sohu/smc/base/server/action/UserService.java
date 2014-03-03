package com.sohu.smc.base.server.action;

import com.sohu.smc.base.server.modle.User;
import com.sohu.smc.redis.SmcJedis;
import com.sohu.smc.redis.SmcJedisFactory;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 1/8/14
 * Time: 3:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserService {
    static SmcJedis loginRedis = SmcJedisFactory.getInstance("counter");
    public static boolean exsist(String userName){
        return loginRedis.hexists("manage_user_"+userName, "userName");
    }

    public static String login(String userName, String password){
        String key = "manage_user_"+userName;
        boolean verify = Boolean.valueOf(loginRedis.hget(key, "verify"));
        if(!verify){
            return "@none-verify";
        }
        String passInDB = loginRedis.hget(key, "password");
        if(StringUtils.equals(passInDB, password)){
            return loginRedis.hget(key, "name");
        }
        return null;
    }

    public static boolean regist(User user){
        String key = "manage_user_" + user.getUserName();
        if(exsist(user.getUserName())) return false;
        loginRedis.hset(key, "userName", user.getUserName());
        loginRedis.hset(key, "password", user.getPassword());
        loginRedis.hset(key, "name", user.getName());
        loginRedis.sadd("manage_user_all_keys", key);
        return true;
    }

    public static boolean modify(User user){
        String key = "manage_user_" + user.getUserName();
        if(!exsist(user.getUserName())) return false;
        loginRedis.hset(key, "password", user.getPassword());
        return true;
    }

    public static boolean verify(String userName){
        String key = "manage_user_" + userName;
        if(!exsist(userName)) return false;
        loginRedis.hset(key, "verify", "true");
        return true;
    }

    public static boolean del(String userName){
        String key = "manage_user_" + userName;
        if(!exsist(userName)) return false;
        loginRedis.hdel(key, "userName","password","name","verify");
        loginRedis.srem("manage_user_all_keys", key);
        return true;
    }


    public static List<User> getAllUser(){
        Set<String> allUserKeys = loginRedis.smembers("manage_user_all_keys");
        List<User> users = new ArrayList<User>();
        for(String each : allUserKeys){
            Map<String,String> eachUser = loginRedis.hgetAll(each);
            String userName = eachUser.get("userName");
            String name = eachUser.get("name");
            String verify = eachUser.get("verify");

            User user = new User();
            user.setUserName(userName);
            user.setName(name);
            user.setVerify(Boolean.valueOf(verify));
            users.add(user);
        }
        return users;
    }
}
