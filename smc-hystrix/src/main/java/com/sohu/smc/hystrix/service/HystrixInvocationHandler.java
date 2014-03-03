package com.sohu.smc.hystrix.service;

import com.netflix.hystrix.HystrixCommand;
import com.sohu.smc.hystrix.annotation.HystrixClassHandler;
import com.sohu.smc.hystrix.annotation.HystrixHandler;
import com.sohu.smc.hystrix.annotation.HystrixIgnoreHandler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 12/2/13
 * Time: 3:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class HystrixInvocationHandler implements InvocationHandler {

    Object clazz;
    HystrixClassHandler hystrixClassHandler;
    static final Logger LOG = LoggerFactory.getLogger(HystrixInvocationHandler.class);

    public HystrixInvocationHandler(Object clazz) {
        this.clazz = clazz;
        //先获取被代理类的标签，如果没有再到其接口中去找，如果找到多个，则取第一个。
        this.hystrixClassHandler = clazz.getClass().getAnnotation(HystrixClassHandler.class);
        if(this.hystrixClassHandler == null){
            Class[] interfaces = clazz.getClass().getInterfaces();
            if(interfaces != null){
                for(Class each : interfaces){
                    this.hystrixClassHandler = (HystrixClassHandler) each.getAnnotation(HystrixClassHandler.class);
                    if(this.hystrixClassHandler != null) break;
                }
            }
        }
    }

    @Override
    public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
        HystrixHandler handler = method.getAnnotation(HystrixHandler.class);
        HystrixIgnoreHandler ignoreHandler = method.getAnnotation(HystrixIgnoreHandler.class);
        if ((this.hystrixClassHandler != null || handler != null) && ignoreHandler == null) {
            String commandGroup = null;
            String commandKey = null;
            HystrixFallBack fallBack = null;
            HystrixCommand hystrixCommand = null;

            if (handler != null) {
                commandGroup = handler.commandGroup();
                commandKey = handler.commandKey();
                hystrixCommand = new HystrixMethodCommand(handler) {
                    @Override
                    protected Object run() throws Exception {
                        method.setAccessible(true);
                        return method.invoke(clazz, args);
                    }

                    @Override
                    protected Object getFallback() {
                        Class returnType = method.getReturnType();

                        return getDefaultValue(returnType);
                    }
                };
            } else if (this.hystrixClassHandler != null) {
                commandGroup = this.hystrixClassHandler.commandGroup();
                commandKey = this.clazz.getClass().getSimpleName() + " " + method.getName();
                hystrixCommand = new HystrixClassCommand(this.hystrixClassHandler, commandKey) {
                    @Override
                    protected Object run() throws Exception {
                        return method.invoke(clazz, args);
                    }

                    @Override
                    protected Object getFallback() {
                        Class returnType = method.getReturnType();
                        return getDefaultValue(returnType);
                    }
                };
            }

            if( args != null){
                for (Object each : args) {
                    if (each instanceof HystrixFallBack) {
                        fallBack = (HystrixFallBack) each;
                    }
                }
            }

            Object result = hystrixCommand.execute();
            if (!hystrixCommand.isSuccessfulExecution()) {
                StringBuilder sb = new StringBuilder("[smc-hystrix]:HystrixCommand execute failed.class=")
                        .append(clazz.getClass().getName()).append(",method=").append(method.getName())
                        .append(",commandGroup=").append(commandGroup).append(",commandKey=").append(commandKey).append(",reason=[");
                Throwable throwable = null;
                if (hystrixCommand.getFailedExecutionException() != null) {
                    sb.append("exception,");
                    throwable = hystrixCommand.getFailedExecutionException().getCause();
                    if (fallBack != null) {
                        fallBack.exception();
                        fallBack.setThrowable(throwable);
                    }
                }
                if (hystrixCommand.isResponseFromFallback()) {
                    sb.append("fallback、");
                    if(fallBack != null){
                        fallBack.fallBack();
                    }
                }
                if (hystrixCommand.isResponseTimedOut()) {
                    sb.append("timeout、");
                }
                if (hystrixCommand.isResponseShortCircuited()) {
                    sb.append("shortCircuited、");
                }
                if (hystrixCommand.isResponseRejected()) {
                    sb.append("rejected、");
                }
                if (hystrixCommand.isCircuitBreakerOpen()) {
                    sb.append("breakerOpen、");
                }
                sb.deleteCharAt(sb.length() - 1);
                sb.append("]");
                sb.append(",time=" + hystrixCommand.getExecutionTimeInMilliseconds());
                if (throwable != null) {
                    LOG.error(sb.toString(), hystrixCommand.getFailedExecutionException().getCause());
                } else {
                    LOG.error(sb.toString());
                }
            }
            return result;
        }
        Object result = method.invoke(clazz, args);
        return result;
    }

    private static Object getDefaultValue(Class returnType) {
        if (StringUtils.equals(returnType.getName() ,"int")) return 0;
        if (StringUtils.equals(returnType.getName() ,"long")) return 0l;
        if (StringUtils.equals(returnType.getName() ,"double")) return 0.0;
        if (StringUtils.equals(returnType.getName() ,"boolean")) return false;
        return null;
    }

}
