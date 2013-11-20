package com.sohu.smc.counter.model;

import com.netflix.config.ConfigurationManager;
import com.sohu.smc.counter.common.JedisFactory;
import com.sohu.smc.counter.conf.SmcCounterConfig;
import org.apache.commons.lang.StringUtils;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 10/15/13
 * Time: 11:37 AM
 * To change this template use File | Settings | File Templates.
 */
public class Counter {
    public static String env = ConfigurationManager.getDeploymentContext().getDeploymentEnvironment();
    public static final JedisFactory COUNTER = new JedisFactory(
            (StringUtils.equals(env, "online")) ? "smc_counter_pool" : "smc_counter_pool_test");
}
