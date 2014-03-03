package test.hystrix.annotation;

import com.sohu.smc.hystrix.service.HystrixFallBack;
import com.sohu.smc.hystrix.service.HystrixMetricSchedule;
import com.sohu.smc.hystrix.service.HystrixProxyFactory;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 12/2/13
 * Time: 3:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class HystrixHandleTestImpl implements HystrixHandleTest{
    public HystrixHandleTestImpl(){}

    public String method(){
        return "HystrixHandleTest.method";
    }


    public static void main(String[] args) throws Exception {
        HystrixMetricSchedule.start();
        HystrixHandleTestImpl test = new HystrixHandleTestImpl();
        HystrixHandleTest proxy = new HystrixProxyFactory<HystrixHandleTest>().getHystrixProxy(test);
//        System.out.println(proxy.method(1000, fallBack) + "" + fallBack.isFall() + ", " + fallBack.isException());
        for(int i=0; i<100000;i++){
            Thread.sleep(100);
            HystrixFallBack fallBack = new HystrixFallBack();
            System.out.print(proxy.method("String" + i, fallBack));
            System.out.println("," + fallBack.isFall() + ", " + fallBack.isException());
//            System.out.println("fallback="+fallBack.isFall());
//            System.out.println(proxy.method(1000, fallBack));
//            System.out.println("fallback="+fallBack.isFall());
//            System.out.println(proxy.method(10000000000000000l, fallBack));
//            System.out.println("fallback="+fallBack.isFall());
//            System.out.println(proxy.method(new Double(1000.00), fallBack));
//            System.out.println("fallback="+fallBack.isFall());
//            System.out.println(proxy.method(false, fallBack));
//            System.out.println("fallback="+fallBack.isFall());
        }


    }

    Random random = new Random();
    public String method(String string, HystrixFallBack fallBack) throws Exception {
        if(random.nextInt(100) > 50){
            throw new MyException("Error");
        }
        return string;
    }

    public Integer method(int i, HystrixFallBack fallBack) throws Exception {
        if(random.nextInt(100) > 50){
            throw new MyException("Error");
        }
        return i;
    }

    public Long method(long l, HystrixFallBack fallBack) throws Exception {
        if(random.nextInt(100) > 30){
            throw new MyException("Error");
        }
        return l;
    }

    public Double method(Double d, HystrixFallBack fallBack) throws Exception {
        if(random.nextInt(100) > 30){
            throw new MyException("Error");
        }
        return d;
    }

    public Boolean method(Boolean l, HystrixFallBack fallBack) throws Exception {
        if(random.nextInt(100) > 30){
            throw new MyException("Error");
        }
        return l;
    }
}
