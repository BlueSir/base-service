package com.nsq;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 10/17/13
 * Time: 11:35 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ConsumerListener {
    public boolean excute(Message message);
}
