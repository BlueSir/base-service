package com.sohu.smc.hystrix.service;

import java.lang.reflect.Proxy;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 12/2/13
 * Time: 3:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class HystrixProxyFactory<T> {

    public HystrixProxyFactory(){}
    public T getHystrixProxy(T clazz){
        return (T)Proxy.newProxyInstance(
                clazz.getClass().getClassLoader(),
                clazz.getClass().getInterfaces(),
                new HystrixInvocationHandler(clazz));
    }
}
