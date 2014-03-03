package test.hystrix.annotation;

import com.sohu.smc.hystrix.annotation.HystrixClassHandler;
import com.sohu.smc.hystrix.service.HystrixFallBack;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 12/2/13
 * Time: 3:38 PM
 * To change this template use File | Settings | File Templates.
 */
@HystrixClassHandler(
        commandGroup = "group",
        circuitBreakerRequestVolumeThreshold = 100

)
public interface HystrixClassHandleTest {
    public String method(String string, HystrixFallBack fallBack) throws Exception;

    public int method(int i, HystrixFallBack fallBack) throws Exception;

    public long method(long l, HystrixFallBack fallBack) throws Exception;

    public double method(Double d, HystrixFallBack fallBack) throws Exception;

    public boolean method(Boolean l, HystrixFallBack fallBack) throws Exception;

}
