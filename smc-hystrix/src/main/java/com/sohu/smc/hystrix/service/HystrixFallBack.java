package com.sohu.smc.hystrix.service;

/**
 * Created with IntelliJ IDEA.
 * User: xiaocaijing
 * Date: 13-9-11
 * Time: 下午5:32
 */
public class HystrixFallBack {
    private boolean isFall = false;
    private boolean isException = false;
    private Throwable throwable = null;
    public void fallBack(){
        isFall = true;
    }

    public boolean isFall(){
        return isFall;
    }

    public void exception(){
        this.isException = true;
    }

    public boolean isException(){
        return isException;
    }

    public void setThrowable(Throwable throwable){
        this.throwable = throwable;
    }

    public Throwable getThrowable(){
        return this.throwable;
    }

}
