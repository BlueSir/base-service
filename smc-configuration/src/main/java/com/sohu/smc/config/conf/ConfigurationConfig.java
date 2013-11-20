package com.sohu.smc.config.conf;

import com.sohu.smc.config.model.AppConfiguration;

/**
 * Created with IntelliJ IDEA.
 * User: xiaocaijing
 * Date: 13-9-10
 * Time: 下午5:13
 */
public class ConfigurationConfig extends AppConfiguration {
    public static final String CONFIGURATION_ZOO_SERVER = getString("smc.configuration.zk.server","").get();

    public static void main(String args[]){
        System.out.println(getString("smc.configuration.zk.server","").get());
    }
}
