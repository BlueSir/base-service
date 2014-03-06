package com.smc.lively.test;

import com.smc.lively.enums.LivelyItemEnum;
import com.smc.lively.service.DataStore;
import com.smc.lively.service.DataStoreFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 3/6/14
 * Time: 4:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class DataStoreTest {

    public static void main(String[] args){
        DataStore dataStore = DataStoreFactory.getInstance(LivelyItemEnum.CID_TEST);
        Set<Long> items = new HashSet<Long>();
        items.add(1000l);
        items.add(1003l);
        items.add(1004l);
        dataStore.createTable();
        dataStore.batchInsert(items);

        Set<Long> set = dataStore.queryAll();
        for(Long each : set){
            System.out.println(each);
        }
    }
}
