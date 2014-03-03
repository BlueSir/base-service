package com.sohu.smc.redis;

import com.sohu.smc.config.model.AppConfiguration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;

import java.util.*;

public class SmcJedis {
    static final Logger LOG = LoggerFactory.getLogger(SmcJedis.class);
    ShardedJedisPool clientMaster = null;
    ShardedJedisPool clientSlaver = null;
    boolean failover = false;

    public SmcJedis(){
    }
    public SmcJedis(String module) {
        init(module);
    }

    public void init(final String module){
        Runnable reInit = new Runnable() {
         
            public void run() {
                init(module);
            }
        };
        String version = AppConfiguration.getString(String.format("smc.redis.%s.version", module),"1.0", reInit).get();
        String masterAdd = AppConfiguration.getString(String.format("smc.redis.%s.masterAdd", module),"").get();
        String masterPass = AppConfiguration.getString(String.format("smc.redis.%s.masterPass", module),"").get();
        String slaveAdd = AppConfiguration.getString(String.format("smc.redis.%s.slaveAdd", module),"").get();
        String slavePass = AppConfiguration.getString(String.format("smc.redis.%s.slavePass", module),"").get();

        int poolMaxActive = AppConfiguration.getInt(String.format("smc.redis.%s.pool.maxActive", module),20).get();
        int poolMaxIdle = AppConfiguration.getInt(String.format("smc.redis.%s.pool.maxIdle", module),5).get();
        int poolMaxWait = AppConfiguration.getInt(String.format("smc.redis.%s.pool.maxWait", module),1000).get();
        boolean testOnBorrow = AppConfiguration.getBoolean(String.format("smc.redis.%s.pool.testOnBorrow", module),false).get();
        boolean testOnReturn = AppConfiguration.getBoolean(String.format("smc.redis.%s.pool.testOnReturn", module), false).get();
        boolean testWhileIdle = AppConfiguration.getBoolean(String.format("smc.redis.%s.pool.testWhileIdle", module), false).get();
        int whenExhaustedAction = AppConfiguration.getInt(String.format("smc.redis.%s.pool.whenExhaustedAction", module),-1).get();
        long timeBetweenEvictionRunsMillis = AppConfiguration.getInt(String.format("smc.redis.%s.pool.timeBetweenEvictionRunsMillis", module),-1).get();
        int numTestsPerEvictionRun = AppConfiguration.getInt(String.format("smc.redis.%s.pool.numTestsPerEvictionRun", module),-1).get();
        long minEvictableIdleTimeMillis = AppConfiguration.getLong(String.format("smc.redis.%s.pool.minEvictableIdleTimeMillis", module),-1).get();
        long softMinEvictableIdleTimeMillis = AppConfiguration.getLong(String.format("smc.redis.%s.pool.softMinEvictableIdleTimeMillis", module),-1).get();

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxActive(poolMaxActive);
        jedisPoolConfig.setMaxIdle(poolMaxIdle);
        jedisPoolConfig.setMaxWait(poolMaxWait);
        jedisPoolConfig.setTestOnBorrow(testOnBorrow);
        jedisPoolConfig.setTestOnReturn(testOnReturn);
        jedisPoolConfig.setTestWhileIdle(testWhileIdle);
        if(whenExhaustedAction > 0){
            jedisPoolConfig.setWhenExhaustedAction((byte)whenExhaustedAction);
        }
        if(timeBetweenEvictionRunsMillis > 0){
            jedisPoolConfig.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        }
        if(numTestsPerEvictionRun > 0){
            jedisPoolConfig.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
        }
        if(minEvictableIdleTimeMillis > 0){
            jedisPoolConfig.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        }
        if(softMinEvictableIdleTimeMillis > 0){
            jedisPoolConfig.setSoftMinEvictableIdleTimeMillis(softMinEvictableIdleTimeMillis);
        }

        try {
            clientMaster = initialShardedPool(jedisPoolConfig, masterAdd, masterPass);
            LOG.info("[smc-redis]:Init redis instance.module=" + module + ",masterAdd=" + masterAdd + ",version=" + version);
            if (StringUtils.isNotBlank(slaveAdd)){
                clientSlaver = initialShardedPool(jedisPoolConfig, slaveAdd, slavePass);
                LOG.info("[smc-redis]:Init redis instance.module="+module+",slaveAdd="+slaveAdd+",version="+version);
                this.failover = true;
            } else {
                clientSlaver = null;
                this.failover = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 初始化切片池
     */
    private ShardedJedisPool initialShardedPool(JedisPoolConfig jedisPoolConfig, String conn, String password) {

        if (jedisPoolConfig == null) {

            // 设置默认配置
            jedisPoolConfig = new JedisPoolConfig();
            jedisPoolConfig.setMaxActive(20);
            jedisPoolConfig.setMaxIdle(5);
            jedisPoolConfig.setMaxWait(1000l);
            jedisPoolConfig.setTestOnBorrow(false);
        }

        List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
        String[] __conns = conn.split(",");
        for (String __conn : __conns) {
            JedisShardInfo info = new JedisShardInfo(__conn.split(":")[0], Integer
                    .parseInt(__conn.split(":")[1]), "master");
            if (StringUtils.isNotBlank(password)) {
                info.setPassword(password);
            }
            shards.add(info);
        }
        // 构造池
        return new ShardedJedisPool(jedisPoolConfig, shards);
    }

    public String get(String key) {
        ShardedJedis jedis = null;
        try {
            jedis = clientMaster.getResource();
            return jedis.get(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                clientMaster.returnResource(jedis);
            }
        }
        return null;
    }

    public String set(String key, String value) {
        String result = null;
        result = set(key, value, clientMaster);
        if (this.failover && clientSlaver != null)
            result = set(key, value, clientSlaver);
        return result;
    }

    public long zadd(String key, double score, String member) {
        long result = -1;
        result = zadd(key, score, member, clientMaster);
        if (this.failover && clientSlaver != null)
            result = zadd(key, score, member, clientSlaver);
        return result;

    }

    public int zadd(String key, Map<Double, String> members) {
        int result = -1;
        result = zadd(key, members, clientMaster);
        if (this.failover && clientSlaver != null)
            result = zadd(key, members, clientSlaver);
        return result;
    }

    public long zrem(String key, String member) {
        long result = -1;
        result = zrem(key, member, clientMaster);
        if (this.failover && clientSlaver != null)
            result = zrem(key, member, clientSlaver);
        return result;

    }

    public long zrem(String key, List<String> members) {
        long result = -1;
        result = zrem(key, members, clientMaster);
        if (this.failover && clientSlaver != null)
            result = zrem(key, members, clientSlaver);
        return result;
    }

    public long zcard(String key) {
        return zcard(key, clientMaster);
    }

    public long zcount(String key, double min, double max) {
        return zcount(key, min, max, clientMaster);
    }

    public double zscore(String key, String member) {
        return zscore(key, member, clientMaster);
    }

    public Set<String> zrange(String key, int start, int end) {
        return zrange(key, start, end, clientMaster);
    }

    public Set<Tuple> zrangeWithScores(String key, int start, int end) {
        return zrangeWithScores(key, start, end, clientMaster);
    }

    public Set<String> zrangeByScore(String key, double min, double max) {
        return zrangeByScore(key, min, max, clientMaster);
    }

    public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max) {
        return zrangeByScoreWithScores(key, min, max, clientMaster);
    }

    public Set<String> zrangeByScore(String key, double min, double max,
                                     int offset, int count) {
        return zrangeByScore(key, min, max, offset, count, clientMaster);
    }

    public Set<Tuple> zrangeByScoreWithScores(String key, double min,
                                              double max, int offset, int count) {
        return zrangeByScoreWithScores(key, min, max, offset, count,
                clientMaster);
    }

    public Set<String> zrevrange(String key, int start, int end) {
        return zrevrange(key, start, end, clientMaster);
    }

    public Set<Tuple> zrevrangeWithScores(String key, int start, int end) {
        return zrevrangeWithScores(key, start, end, clientMaster);
    }

    public Set<String> zrevrangeByScore(String key, double min, double max) {
        return zrevrangeByScore(key, min, max, clientMaster);
    }

    public Set<Tuple> zrevrangeByScoreWithScores(String key, double min,
                                                 double max) {
        return zrevrangeByScoreWithScores(key, min, max, clientMaster);
    }

    public Set<String> zrevrangeByScore(String key, double min, double max,
                                        int offset, int count) {
        return zrevrangeByScore(key, min, max, offset, count, clientMaster);
    }

    public Set<Tuple> zrevrangeByScoreWithScores(String key, double min,
                                                 double max, int offset, int count) {
        return zrevrangeByScoreWithScores(key, min, max, offset, count,
                clientMaster);
    }

    public long zrank(String key, String member) {
        return zrank(key, member, clientMaster);
    }

    public long zrevrank(String key, String member) {
        return zrevrank(key, member, clientMaster);
    }

    public long zremrangeByRank(String key, int start, int end) {
        long result = -1;
        result = zremrangeByRank(key, start, end, clientMaster);
        if (this.failover && clientSlaver != null) {
            result = zremrangeByRank(key, start, end, clientSlaver);
        }
        return result;
    }

    public long zremrangeByScore(String key, double min, double max) {
        long result = -1;
        result = zremrangeByScore(key, min, max, clientMaster);
        if (this.failover && clientSlaver != null) {
            result = zremrangeByScore(key, min, max, clientSlaver);
        }
        return result;
    }

    public long rpush(String key, String value) {
        long result = rpush(key, value, clientMaster);
        if (this.failover && clientSlaver != null) {
            result = rpush(key, value, clientSlaver);
        }
        return result;
    }

    public long rpush(String key, List<String> values) {
        long result = rpush(key, values, clientMaster);
        if (this.failover && clientSlaver != null) {
            result = rpush(key, values, clientSlaver);
        }
        return result;
    }

    public long llen(String key) {
        return llen(key, clientMaster);
    }

    public List<String> lrange(String key, int start, int end) {
        return lrange(key, start, end, clientMaster);
    }

    public long expire(String key, int seconds) {
        long result = this.expire(key, seconds, clientMaster);
        if (this.failover && clientSlaver != null) {
            result = this.expire(key, seconds, clientSlaver);
        }
        return result;
    }

    public long del(String key) {
        long result = this.del(key, clientMaster);
        if (this.failover && clientSlaver != null) {
            result = this.del(key, clientSlaver);
        }
        return result;
    }

    public long del(List<String> keys) {
        long result = this.del(keys, clientMaster);
        if (this.failover && clientSlaver != null) {
            result = this.del(keys, clientSlaver);
        }
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

    public long zadd(String key, double score, String member,
                     ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.zadd(key, score, member);
        } catch (Exception e) {

            return -1;
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public int zadd(String key, Map<Double, String> members,
                    ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            int count = 0;
            Iterator<Double> memIte = members.keySet().iterator();
            while (memIte.hasNext()) {
                double score = memIte.next();
                String member = members.get(score);
                count += jedis.zadd(key, score, member);
            }
            return count;
        } catch (Exception e) {

            return -1;
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public long zrem(String key, String member, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.zrem(key, member);
        } catch (Exception e) {

            return -1;
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public long zrem(String key, List<String> members, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            String[] array = (String[]) members.toArray();
            return jedis.zrem(key, array);
        } catch (Exception e) {
            return -1;
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public long zcard(String key, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.zcard(key);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public long zcount(String key, double min, double max, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.zcount(key, min, max);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public double zscore(String key, String member, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.zscore(key, member);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public Set<String> zrange(String key, int start, int end,
                              ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.zrange(key, start, end);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public Set<Tuple> zrangeWithScores(String key, int start, int end,
                                       ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.zrangeWithScores(key, start, end);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public Set<String> zrangeByScore(String key, double min, double max,
                                     ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.zrangeByScore(key, min, max);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public Set<Tuple> zrangeByScoreWithScores(String key, double min,
                                              double max, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.zrangeByScoreWithScores(key, min, max);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public Set<String> zrangeByScore(String key, double min, double max,
                                     int offset, int count, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.zrangeByScore(key, min, max, offset, count);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public Set<Tuple> zrangeByScoreWithScores(String key, double min,
                                              double max, int offset, int count, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.zrangeByScoreWithScores(key, min, max, offset, count);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public Set<String> zrevrange(String key, int start, int end,
                                 ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.zrevrange(key, start, end);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public Set<Tuple> zrevrangeWithScores(String key, int start, int end,
                                          ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.zrevrangeWithScores(key, start, end);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public Set<String> zrevrangeByScore(String key, double min, double max,
                                        ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.zrevrangeByScore(key, max, min);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public Set<Tuple> zrevrangeByScoreWithScores(String key, double min,
                                                 double max, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.zrevrangeByScoreWithScores(key, max, min);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public Set<String> zrevrangeByScore(String key, double min, double max,
                                        int offset, int count, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.zrevrangeByScore(key, max, min, offset, count);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public Set<Tuple> zrevrangeByScoreWithScores(String key, double min,
                                                 double max, int offset, int count, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.zrevrangeByScoreWithScores(key, max, min, offset,
                    count);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public long zrank(String key, String member, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.zrank(key, member);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public long zrevrank(String key, String member, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.zrevrank(key, member);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public long zremrangeByRank(String key, int start, int end,
                                ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.zremrangeByRank(key, start, end);
        } catch (Exception e) {
            return -1;
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public long zremrangeByScore(String key, double min, double max,
                                 ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.zremrangeByScore(key, min, max);
        } catch (Exception e) {
            return -1;
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public long rpush(String key, String value, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.rpush(key, value);
        } catch (Exception e) {
            return -1;
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public long rpush(String key, List<String> values, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            long length = 0;
            for (String value : values) {
                length = jedis.rpush(key, value);
            }
            return length;
        } catch (Exception e) {
            return -1;
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public long llen(String key, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.llen(key);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public List<String> lrange(String key, int start, int end,
                               ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lrange(key, start, end);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public String rpop(String key, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.rpop(key);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public String rpop(String key) {
        String result = rpop(key, clientMaster);
        if (this.failover && clientSlaver != null)
            result = rpop(key, clientSlaver);
        return result;
    }

    public void ltrim(String key, ShardedJedisPool pool, int start, int end) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.ltrim(key, start, end);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public long lpush(String key, String value, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.lpush(key, value);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public long lpush(String key, String value) {
        long result = lpush(key, value, clientMaster);
        if (this.failover && clientSlaver != null)
            result = lpush(key, value, clientSlaver);
        return result;
    }

    public void ltrim(String key, int start, int end) {
        ltrim(key, clientMaster, start, end);
        if (this.failover && clientSlaver != null)
            ltrim(key, clientSlaver, start, end);
    }

    public long lpush(String key, List<String> values, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            long length = 0;
            for (String value : values) {
                length = jedis.lpush(key, value);
            }
            return length;
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public long lpush(String key, List<String> values) {
        long result = lpush(key, values, clientMaster);
        if (this.failover && clientSlaver != null)
            result = lpush(key, values, clientSlaver);
        return result;
    }

    public String lpop(String key, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.lpop(key);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public String lpop(String key) {
        String result = lpop(key, clientMaster);
        if (this.failover && clientSlaver != null)
            result = lpop(key, clientSlaver);
        return result;
    }

    public long srem(String key, String member, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.srem(key, member);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public long srem(String key, String member) {
        long result = srem(key, member, clientMaster);
        if (this.failover && clientSlaver != null)
            result = srem(key, member, clientSlaver);
        return result;
    }

    public long srem(String key, List<String> members, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            int count = 0;
            Iterator<String> memIte = members.iterator();
            while (memIte.hasNext()) {
                String member = memIte.next();
                count += jedis.srem(key, member);
            }
            return count;
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public long srem(String key, List<String> members) {
        long result = srem(key, members, clientMaster);
        if (this.failover && clientSlaver != null)
            result = srem(key, members, clientSlaver);
        return result;
    }

    public Set<String> smembers(String key, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.smembers(key);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public Set<String> smembers(String key) {
        return this.smembers(key, clientMaster);
    }

    public boolean sismember(String key, String member, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.sismember(key, member);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public boolean sismember(String key, String member) {
        return this.sismember(key, member, clientMaster);
    }

    public long expire(String key, int seconds, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.expire(key, seconds);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public long del(String key, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.del(key);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public long del(List<String> keys, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            int count = 0;
            Iterator<String> keyIte = keys.iterator();
            while (keyIte.hasNext()) {
                String key = keyIte.next();
                count += jedis.del(key);
            }
            return count;
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public long scard(String key, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.scard(key);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public long scard(String key) {
        return this.scard(key, clientMaster);
    }

    public long sadd(String key, String member, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            return jedis.sadd(key, member);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public long sadd(String key, String member) {
        long result = this.sadd(key, member, clientMaster);
        if (this.failover && clientSlaver != null)
            result = sadd(key, member, clientSlaver);
        return result;

    }

    public long sadd(String key, List<String> members, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();

            int count = 0;
            Iterator<String> memIte = members.iterator();
            while (memIte.hasNext()) {
                String member = memIte.next();
                count += jedis.sadd(key, member);
            }
            return count;
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public long incr(String key) {
        long ret = this.incrBy(key, 1, this.clientMaster);
        if(this.failover && this.clientSlaver != null){
            ret = this.incrBy(key, 1, this.clientSlaver);
        }
        return ret;
    }

    public long incrBy(String key, long increment) {
        long ret = this.incrBy(key, increment, this.clientMaster);
        if(this.failover && this.clientSlaver != null){
            ret = this.incrBy(key, increment, this.clientSlaver);
        }
        return ret;
    }

    public long incrBy(String key, long increment, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.incrBy(key, increment);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public long decr(String key) {
        long ret = this.decrBy(key, 1, this.clientMaster);
        if(this.failover && this.clientSlaver != null){
            ret = this.decrBy(key, 1, this.clientSlaver);
        }
        return ret;
    }

    public long decrBy(String key, long decrement) {
        long ret = this.decrBy(key, decrement, this.clientMaster);
        if(this.failover && this.clientSlaver != null){
            ret = this.decrBy(key, decrement, this.clientSlaver);
        }
        return ret;
    }

    public long decrBy(String key, long decrement, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.decrBy(key, decrement);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public long sadd(String key, List<String> members) {
        long result = this.sadd(key, members, clientMaster);
        if (this.failover && clientSlaver != null)
            result = sadd(key, members, clientSlaver);
        return result;
    }


    public Long hset(String key, String field, String value) {

        long result = this.hset(key, field, value, clientMaster);
        if(this.failover && clientSlaver != null){
            result = this.hset(key, field, value, clientSlaver);
        }
        return result;
    }

    public Long hset(String key, String field, String value, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.hset(key, field, value);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public String hget(String key, String field) {
        ShardedJedis jedis = null;
        try {
            jedis = clientMaster.getResource();
            return jedis.hget(key, field);
        } finally {
            if (jedis != null) {
                clientMaster.returnResource(jedis);
            }
        }
    }

    public Long hsetnx(String key, String field, String value) {
        long result = this.hsetnx(key, field, value, clientMaster);
        if(this.failover && clientSlaver != null){
            result = this.hsetnx(key, field, value, clientSlaver);
        }
        return result;
    }

    public Long hsetnx(String key, String field, String value, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.hsetnx(key, field, value);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }


    public String hmset(String key, Map<String, String> hash) {
        String result = this.hmset(key, hash, clientMaster);
        if(this.failover && clientSlaver != null){
            result = this.hmset(key, hash, clientSlaver);
        }
        return result;
    }
    public String hmset(String key, Map<String, String> hash, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.hmset(key, hash);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public List<String> hmget(String key, String... fields) {
        ShardedJedis jedis = null;
        try {
            jedis = clientMaster.getResource();
            return jedis.hmget(key, fields);
        } finally {
            if (jedis != null) {
                clientMaster.returnResource(jedis);
            }
        }
    }

    public Long hincrBy(String key, String field, long value) {
        Long result = this.hincrBy(key, field, value, clientMaster);
        if(this.failover && clientSlaver != null){
            result = this.hincrBy(key, field, value, clientSlaver);
        }
        return result;
    }

    public Long hincrBy(String key, String field, long value, ShardedJedisPool pool) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.hincrBy(key, field, value);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public Boolean hexists(String key, String field) {
        ShardedJedis jedis = null;
        try {
            jedis = clientMaster.getResource();
            return jedis.hexists(key, field);
        } finally {
            if (jedis != null) {
                clientMaster.returnResource(jedis);
            }
        }
    }

    public Long hdel(String key, String... fields) {
        Long result = this.hdel(clientMaster, key, fields);
        if(this.failover && clientSlaver != null){
            result = this.hdel(clientSlaver, key, fields);
        }
        return result;
    }

    public Long hdel(ShardedJedisPool pool, String key, String... fields) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.hdel(key, fields);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

 
    public Long hlen(String key) {
        ShardedJedis jedis = null;
        try {
            jedis = clientMaster.getResource();
            return jedis.hlen(key);
        } finally {
            if (jedis != null) {
                clientMaster.returnResource(jedis);
            }
        }
    }

 
    public Set<String> hkeys(String key) {
        ShardedJedis jedis = null;
        try {
            jedis = clientMaster.getResource();
            return jedis.hkeys(key);
        } finally {
            if (jedis != null) {
                clientMaster.returnResource(jedis);
            }
        }
    }

 
    public List<String> hvals(String key) {
        ShardedJedis jedis = null;
        try {
            jedis = clientMaster.getResource();
            return jedis.hvals(key);
        } finally {
            if (jedis != null) {
                clientMaster.returnResource(jedis);
            }
        }
    }

 
    public Map<String, String> hgetAll(String key) {
        ShardedJedis jedis = null;
        try {
            jedis = clientMaster.getResource();
            return jedis.hgetAll(key);
        } finally {
            if (jedis != null) {
                clientMaster.returnResource(jedis);
            }
        }
    }

    public Long rpush(String key, String... strings) {
        Long result = this.rpush(clientMaster, key, strings);
        if(this.failover && clientSlaver != null){
            result = this.rpush(clientSlaver, key, strings);
        }
        return result;
    }

    public Long rpush(ShardedJedisPool pool, String key, String... strings) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.rpush(key, strings);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public Long lpush(String key, String... strings) {
        Long result = this.lpush(clientMaster, key, strings);
        if(this.failover && clientSlaver != null){
            result = this.lpush(clientSlaver, key, strings);
        }
        return result;
    }

    public Long lpush(ShardedJedisPool pool, String key, String... strings) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lpush(key, strings);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public List<String> lrange(String key, long start, long end) {
        ShardedJedis jedis = null;
        try {
            jedis = clientMaster.getResource();
            return jedis.lrange(key, start, end);
        } finally {
            if (jedis != null) {
                clientMaster.returnResource(jedis);
            }
        }
    }

 
    public String ltrim(String key, long start, long end) {
        String result = this.ltrim(clientMaster, key, start, end);
        if(this.failover && clientSlaver != null){
            result = this.ltrim(clientSlaver, key, start, end);
        }
        return result;
    }

    public String ltrim(ShardedJedisPool pool, String key, long start, long end) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.ltrim(key, start, end);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

 
    public String lindex(String key, long index) {
        ShardedJedis jedis = null;
        try {
            jedis = clientMaster.getResource();
            return jedis.lindex(key, index);
        } finally {
            if (jedis != null) {
                clientMaster.returnResource(jedis);
            }
        }
    }

 
    public String lset(String key, long index, String value) {
        String result = this.lset(clientMaster, key, index, value);
        if(this.failover && clientSlaver != null){
            result = this.lset(clientSlaver, key, index, value);
        }
        return result;
    }

    public String lset(ShardedJedisPool pool, String key, long index, String value) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lset(key, index, value);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

 
    public Long lrem(String key, long count, String value) {
        Long result = this.lrem(clientMaster, key, count, value);
        if(this.failover && clientSlaver != null){
            result = this.lrem(clientSlaver, key, count, value);
        }
        return result;
    }

    public Long lrem(ShardedJedisPool pool, String key, long count, String value) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lrem(key, count, value);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }


    public String spop(String key) {
        String result = this.spop(clientMaster, key);
        if(this.failover && clientSlaver != null){
            result = this.spop(clientSlaver, key);
        }
        return result;
    }

    public String spop(ShardedJedisPool pool, String key) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.spop(key);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

 
    public String srandmember(String key) {
        ShardedJedis jedis = null;
        try {
            jedis = clientMaster.getResource();
            return jedis.srandmember(key);
        } finally {
            if (jedis != null) {
                clientMaster.returnResource(jedis);
            }
        }
    }


    public Double zincrby(String key, double score, String member) {
        Double result = this.zincrby(clientMaster, key, score, member);
        if(this.failover && clientSlaver != null){
            result = this.zincrby(clientSlaver, key, score, member);
        }
        return result;
    }

    public Double zincrby(ShardedJedisPool pool, String key, double score, String member) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zincrby(key, score, member);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public List<String> sort(String key) {
        List<String> result = this.sort(clientMaster, key);
        if(this.failover && clientSlaver != null){
            result = this.sort(clientSlaver, key);
        }
        return result;
    }

    public List<String> sort(ShardedJedisPool pool, String key) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sort(key);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

 
    public List<String> sort(String key, SortingParams sortingParameters) {
        List<String> result = this.sort(clientMaster, key, sortingParameters);
        if(this.failover && clientSlaver != null){
            result = this.sort(clientSlaver, key, sortingParameters);
        }
        return result;
    }

    public List<String> sort(ShardedJedisPool pool, String key, SortingParams sortingParameters) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sort(key, sortingParameters);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }


    public Long linsert(String key, BinaryClient.LIST_POSITION where, String pivot, String value) {
        Long result = this.linsert(clientMaster, key, where, pivot, value);
        if(this.failover && clientSlaver != null){
            result = this.linsert(clientSlaver, key, where, pivot, value);
        }
        return result;
    }

    public Long linsert(ShardedJedisPool pool, String key, BinaryClient.LIST_POSITION where, String pivot, String value) {
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.linsert(key, where, pivot, value);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    public ShardedJedisPool getMasterClient() {
        return clientMaster;
    }

    public ShardedJedisPool getSlaverClient() {
        return clientSlaver;
    }
}