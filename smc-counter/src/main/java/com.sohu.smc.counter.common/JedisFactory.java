package com.sohu.smc.counter.common;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import java.util.ArrayList;
import java.util.List;

public class JedisFactory {
    static Resource resource = null;
    static BeanFactory ctx = null;
    static {
        init();
    }

    public static synchronized void init() {
        resource = new ClassPathResource("smc-counter-redis.xml");
        ctx = new XmlBeanFactory(resource);
        PropertyPlaceholderConfigurer configurer = ctx.getBean(PropertyPlaceholderConfigurer.class);
        configurer.postProcessBeanFactory((XmlBeanFactory)ctx);
    }

    JedisBean bean = null;
    ShardedJedisPool clientMaster = null;
    ShardedJedisPool clientSlaver = null;

    String module = "";

    JedisPoolConfig jedisPoolConfig;

    public JedisPoolConfig getJedisPoolConfig() {
        return jedisPoolConfig;
    }

    public void setJedisPoolConfig(JedisPoolConfig jedisPoolConfig) {
        this.jedisPoolConfig = jedisPoolConfig;
    }

    /**
     * 初始化切片池
     */
    private ShardedJedisPool initialShardedPool(String conn) {
        List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
        String[] __conns = conn.split(",");
        for (String __conn : __conns) {
            JedisShardInfo jedisSharedInfo = new JedisShardInfo(__conn.split(":")[0], Integer
                    .parseInt(__conn.split(":")[1]), "master");
            if(StringUtils.isNotBlank(bean.getPassword())){
                jedisSharedInfo.setPassword(bean.getPassword());
            }
            shards.add(jedisSharedInfo);
        }
        // 构造池
        return new ShardedJedisPool(this.jedisPoolConfig, shards);
    }

    public JedisFactory(String module) {
        bean = (JedisBean) ctx.getBean(module);
        this.jedisPoolConfig = (JedisPoolConfig) ctx.getBean("jedisPoolConfig");
        try {
            clientMaster = initialShardedPool(bean.getMasterAddress());
            if (bean.getSlaveAddress() != null){
                clientSlaver = initialShardedPool(bean.getSlaveAddress());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.module = module;
    }

    public String get(String key) {

        ShardedJedis jedis = null;
        try {
            if(clientSlaver != null){
                jedis = clientSlaver.getResource();
            } else {
                jedis = clientMaster.getResource();
            }
            String ret = jedis.get(key);
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                if(clientSlaver != null){
                    clientSlaver.returnResource(jedis);
                }else{
                    clientMaster.returnResource(jedis);
                }
            }
        }
        return null;
    }

    public String set(String key, String value) {
        String result = null;
        result = set(key, value, clientMaster);
        return result;
    }

    public String set(String key, String value, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.set(key, value);
        } catch (Exception e) {

            return null;
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public long incr(String key){
        ShardedJedis jedis = null;
        try {
            jedis = clientMaster.getResource();
            return jedis.incr(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                clientMaster.returnResource(jedis);
            }
        }
        return -1;
    }

    public long incrBy(String key, long increment){
        ShardedJedis jedis = null;
        try {
            jedis = clientMaster.getResource();
            return jedis.incrBy(key, increment);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                clientMaster.returnResource(jedis);
            }
        }
        return -1;
    }

    public long decr(String key){
        ShardedJedis jedis = null;
        try {
            jedis = clientMaster.getResource();
            return jedis.decr(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                clientMaster.returnResource(jedis);
            }
        }
        return -1;
    }

    public long decrBy(String key, long decrement){
        ShardedJedis jedis = null;
        try {
            jedis = clientMaster.getResource();
            return jedis.decrBy(key, decrement);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                clientMaster.returnResource(jedis);
            }
        }
        return -1;
    }

    public ShardedJedisPool getMasterClient() {
        return clientMaster;
    }

    public ShardedJedisPool getSlaverClient() {
        return clientSlaver;
    }
}