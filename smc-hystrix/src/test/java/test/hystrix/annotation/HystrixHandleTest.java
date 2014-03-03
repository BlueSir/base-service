package test.hystrix.annotation;

import com.sohu.smc.hystrix.annotation.HystrixHandler;
import com.sohu.smc.hystrix.service.HystrixFallBack;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 12/2/13
 * Time: 3:38 PM
 * To change this template use File | Settings | File Templates.
 */
public interface HystrixHandleTest {
    @HystrixHandler(commandGroup = "testGroup",
                    commandKey = "testItem",
                    threadPool = "threadPool",
                    errorThresholdPercentage = 75,
                    threadPool_coreSize = 20,
                    threadPool_queueSizeRejectionThreshold = 100
    )
    public String method(String string, HystrixFallBack fallBack) throws Exception;

    @HystrixHandler(commandGroup = "testGroup", commandKey = "testItem")
    public Integer method(int i, HystrixFallBack fallBack) throws Exception;

    @HystrixHandler(commandGroup = "testGroup", commandKey = "testItem")
    public Long method(long l, HystrixFallBack fallBack) throws Exception;

    @HystrixHandler(commandGroup = "testGroup", commandKey = "testItem")
    public Double method(Double d, HystrixFallBack fallBack) throws Exception;

    @HystrixHandler(commandGroup = "testGroup", commandKey = "testItem")
    public Boolean method(Boolean l, HystrixFallBack fallBack) throws Exception;

}
