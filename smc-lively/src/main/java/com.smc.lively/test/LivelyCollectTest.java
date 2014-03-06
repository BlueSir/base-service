package com.smc.lively.test;

import com.smc.lively.collect.LivelyCollect;
import com.smc.lively.enums.LivelyItemEnum;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 3/5/14
 * Time: 3:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class LivelyCollectTest{

    static Random random = new Random();
    static long counter = 5000000l;
    public static void main(String[] args) throws InterruptedException {
        while(true){
            long cid = counter++;
            LivelyCollect.collect(LivelyItemEnum.CID_TEST, cid);
            TimeUnit.MILLISECONDS.sleep(100);
        }

    }
}
