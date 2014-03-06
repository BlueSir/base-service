package com.smc.lively.kafka;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 3/5/14
 * Time: 11:37 AM
 * To change this template use File | Settings | File Templates.
 */
public interface MessageExecutor {

    public void execute(Set<String> message);
}
