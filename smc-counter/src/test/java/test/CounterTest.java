package test;

import com.sohu.smc.config.service.SmcConfiguration;
import com.sohu.smc.counter.model.CounterKey;
import com.sohu.smc.counter.service.Counters;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 10/16/13
 * Time: 10:11 AM
 */
public class CounterTest {

    public static void main(String[] args) throws InterruptedException {
        SmcConfiguration.applyPlaceHolder();
        CounterKey key = new CounterKey().append("productId", 1001).append("subId",1447);
//        Counters.CLIENT_SUB_COUNTER.reset(key,1);
        Thread.sleep(3*1000);
//        long start = System.currentTimeMillis();
//        MyThread t1 = new MyThread(key, Action.INCR, 50000);
//        MyThread t2 = new MyThread(key, Action.DECR, 20000);
//        MyThread t3 = new MyThread(key, Action.INCR, 50000);
//        MyThread t4 = new MyThread(key, Action.DECR, 20000);
//        MyThread t5 = new MyThread(key, Action.INCR, 50000);
//        MyThread t6 = new MyThread(key, Action.DECR, 20000);
//        MyThread t7 = new MyThread(key, Action.INCR, 50000);
//        MyThread t8 = new MyThread(key, Action.DECR, 20000);
//
//        t1.start();
//        t2.start();
//        t3.start();
//        t4.start();
//        t5.start();
//        t6.start();
//        t7.start();
//        t8.start();

        while(true){
            System.out.println("incr:"+Counters.CLIENT_SUB_COUNTER.incr(key));
            System.out.println("get:"+Counters.CLIENT_SUB_COUNTER.getCount(key));

            System.out.println("incr:"+Counters.CLIENT_CLOUD_COUNTER.incr(key));
            System.out.println("get:"+Counters.CLIENT_CLOUD_COUNTER.getCount(key));
            Thread.sleep(10 * 1000);
        }
    }

}
    class MyThread extends Thread{

        CounterKey key;
        Action act;
        long count;
        MyThread(CounterKey key, Action act, long count){
            this.key = key;
            this.act = act;
            this.count = count;
        }
        @Override
        public void run() {
            switch(act){
                case INCR: {
                    long start = System.currentTimeMillis();
                    for(int i = 1; i<=this.count;i++){
                        Counters.CLIENT_SUB_COUNTER.incr(this.key);
                    }
                    System.out.println("cost="+(System.currentTimeMillis() - start));
                    break;
                }
                case DECR: {
                    long start = System.currentTimeMillis();
                    for(int i = 1;i<=this.count;i++){
                        Counters.CLIENT_SUB_COUNTER.decr(this.key);
                    }
                    System.out.println("cost="+(System.currentTimeMillis() - start));
                    break;
                }
            }

        }
    }

    enum Action{
        INCR, INCRBY, DECR, DECRBY,RESET,GET;
    }
