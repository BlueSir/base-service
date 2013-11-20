package com.sohu.smc.schedule.core.conf;

import com.sohu.smc.config.model.AppConfiguration;

/**
 * Created with IntelliJ IDEA.
 * User: xiaocaijing
 * Date: 13-9-10
 * Time: 下午5:13
 */
public class SmcScheduleCoreConfig extends AppConfiguration {
    public static final String SCHEDULE_ZK_SERVER = getString("smc.zookeeper.server", "").get();

}
