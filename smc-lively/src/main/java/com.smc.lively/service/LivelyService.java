package com.smc.lively.service;

import com.smc.lively.enums.LivelyItemEnum;
import com.smc.lively.kafka.KafkaConsumer;
import com.smc.lively.kafka.KafkaEnum;
import com.smc.lively.kafka.MessageExecutor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 2/25/14
 * Time: 2:34 PM
 */
public class LivelyService {
    static final Logger LOG = LoggerFactory.getLogger(LivelyService.class);
    static HashMap<LivelyItemEnum, Set<Long>> LIVELY_CACHE = new HashMap<LivelyItemEnum, Set<Long>>();
    private static LivelyService INSTANCE = new LivelyService();
    private LivelyService(){}

    public static void init(LivelyItemEnum livelyItemEnum){
        DataStore dataStore = DataStoreFactory.getInstance(livelyItemEnum);
        if(dataStore.isTableExsist()){
            Set<Long> livelyFromBD = dataStore.queryAll();
            LIVELY_CACHE.put(livelyItemEnum, livelyFromBD);
        }else{
            Set<Long> livelyFromRedis = LivelyRedis.getAllLively(livelyItemEnum);
            LIVELY_CACHE.put(livelyItemEnum, livelyFromRedis);
            dataStore.createTable();
            if(dataStore.batchInsert(livelyFromRedis) == null){
                LOG.error("[init.error]:DataStore batch insert error.");
                System.exit(0);
            }
        }
        try {
            KafkaConsumer addConsumer = new KafkaConsumer("lively", KafkaEnum.LIVELY_ADD_TOPIC.name, 2, new MessageExecutor(){
                @Override
                public void execute(Set<String> message) {
                    System.out.println(message.size());
                    Map<String, Set<Long>> msgs = getStringSetMap(message);
                    Iterator<String> it = msgs.keySet().iterator();
                    while(it.hasNext()){
                        String key = it.next();
                        Set<Long> values = msgs.get(key);
                        LivelyItemEnum itemEnum = LivelyItemEnum.getLivelyItemByName(key);
                        if(itemEnum == null){
                            LOG.error("[addConsumer.execute]:LivelyItemEnum return null enum object.name="+key);
                        } else {
                            Set<Long> cacheItem = LIVELY_CACHE.get(itemEnum);
                            values.removeAll(cacheItem);
                            int ret[] = DataStoreFactory.getInstance(itemEnum).batchInsert(values);
                            if(ret == null){
                                LOG.error("[addConsumer.execute]:Insert item error.name=" + key + ",size=" + values.size());
                            }
                            LIVELY_CACHE.get(itemEnum).addAll(values);
                        }
                    }
                    msgs.clear();
                }
            });
            addConsumer.start();
            KafkaConsumer delConsumer = new KafkaConsumer("lively", KafkaEnum.LIVELY_DEL_TOPIC.name, 2, new MessageExecutor(){
                @Override
                public void execute(Set<String> message) {
                    System.out.println(message.size());
                    Map<String, Set<Long>> msgs = getStringSetMap(message);
                    Iterator<String> it = msgs.keySet().iterator();
                    while(it.hasNext()){
                        String key = it.next();
                        Set<Long> values = msgs.get(key);
                        LivelyItemEnum itemEnum = LivelyItemEnum.getLivelyItemByName(key);
                        if(itemEnum == null){
                            LOG.error("[delConsumer.execute]:LivelyItemEnum return null enum object.name="+key);
                        } else {
                            int ret[] = DataStoreFactory.getInstance(itemEnum).batchDelete(values);
                            if(ret == null){
                                LOG.error("[delConsumer.execute]:Delete item error.name="+key+",size=" + values.size());
                            }
                            LIVELY_CACHE.get(itemEnum).removeAll(values);
                        }
                    }
                    msgs.clear();
                }
            });
            delConsumer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static Map<String, Set<Long>> getStringSetMap(Set<String> message) {
        Map<String,Set<Long>> msgs = new HashMap<String,Set<Long>>();
        for (String each : message){
            String[] strs = StringUtils.split(each, "@");
            if(strs.length <2){
                continue;
            } else {
                String name = strs[0];
                long item = Long.valueOf(strs[1]);
                if(msgs.containsKey(name)){
                    msgs.get(name).add(item);
                } else {
                    Set<Long> hashSet = new HashSet<Long>();
                    hashSet.add(item);
                    msgs.put(name, hashSet);
                }
            }
        }
        return msgs;
    }

    public static LivelyService getInstance(){
        return INSTANCE;
    }

    public Set<Long> getAllLively(LivelyItemEnum livelyItemEnum){
        Set<Long> allLively = LIVELY_CACHE.get(livelyItemEnum);
        if(allLively == null){
            LOG.error("[getAllLively]:"+livelyItemEnum.name+"'s local cache is not init.Please init it first.");
        }
        return allLively;
    }

    public boolean isLively(LivelyItemEnum livelyItemEnum, long item){
        Set<Long> lively = LIVELY_CACHE.get(livelyItemEnum);
        if(lively == null){
            LOG.error("[isLively]:"+livelyItemEnum.name+"'s local cache is not init.Please init it first.");
            return false;
        }else{
            return lively.contains(item);
        }
    }


}
