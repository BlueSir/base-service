package test.hystrix.annotation;

import com.sohu.smc.hystrix.annotation.HystrixClassHandler;
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
public class HystrixClassHandleImplTest implements HystrixClassHandleTest{
    public HystrixClassHandleImplTest(){}

    public String method(){
        return "HystrixHandleTest.method";
    }


    public static void main(String[] args) throws Exception {
        HystrixMetricSchedule.start();
        HystrixClassHandleImplTest test = new HystrixClassHandleImplTest();
        HystrixClassHandleTest proxy = new HystrixProxyFactory<HystrixClassHandleTest>().getHystrixProxy(test);
        HystrixFallBack fallBack = new HystrixFallBack();
        System.out.println(proxy.method(1000, fallBack));
        for(int i=0; i<2000;i++){
            Thread.sleep(100);
            System.out.println(proxy.method("String" + i, fallBack));
            System.out.println("fallback="+fallBack.isFall());
            System.out.println(proxy.method(1000, fallBack));
            System.out.println("fallback="+fallBack.isFall());
            System.out.println(proxy.method(10000000000000000l, fallBack));
            System.out.println("fallback="+fallBack.isFall());
            System.out.println(proxy.method(new Double(1000.00), fallBack));
            System.out.println("fallback="+fallBack.isFall());
            System.out.println(proxy.method(false, fallBack));
            System.out.println("fallback="+fallBack.isFall());
        }


    }

    Random random = new Random();
    public String method(String string, HystrixFallBack fallBack) throws Exception {
        if(random.nextInt(100) > 10){
            throw new MyException("Error");
        }
        return string;
    }

    public int method(int i, HystrixFallBack fallBack) throws Exception {
        if(random.nextInt(100) > 100){
            throw new MyException("Error");
        }
        return i;
    }

    public long method(long l, HystrixFallBack fallBack) throws Exception {
        if(random.nextInt(100) > 30){
            throw new MyException("Error");
        }
        return l;
    }

    public double method(Double d, HystrixFallBack fallBack) throws Exception {
        if(random.nextInt(100) > 30){
            throw new MyException("Error");
        }
        return d;
    }

    public boolean method(Boolean l, HystrixFallBack fallBack) throws Exception {
        if(random.nextInt(100) > 30){
            throw new MyException("Error");
        }
        return l;
    }
}
