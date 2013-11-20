package test.config;

import com.netflix.config.DynamicStringProperty;
import com.sohu.smc.config.model.AppConfiguration;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 11/12/13
 * Time: 10:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class MyConfiguration extends AppConfiguration {

    public static DynamicStringProperty test1 = getString("smc.configuration.test1", "default");

}
