package com.smc.lively.test;

import com.smc.lively.enums.LivelyItemEnum;
import com.smc.lively.service.LivelyService;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 3/5/14
 * Time: 6:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class LivelyServiceTest {

    static long counter = 4000000l;
    public static void main(String[] args) throws InterruptedException {
        LivelyService.init(LivelyItemEnum.CID_TEST);
        for (int i=0;i<100;i++){
            TimeUnit.SECONDS.sleep(2);
            counter ++;
            System.out.println(counter +":" + LivelyService.getInstance().isLively(LivelyItemEnum.CID_3_DAY, 50323423909234l));
        }
    }
}
