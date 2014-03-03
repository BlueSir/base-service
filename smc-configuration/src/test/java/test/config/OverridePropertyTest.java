package test.config;

import com.sohu.smc.config.model.AppConfiguration;
import com.sohu.smc.config.service.SmcConfiguration;
import junit.framework.TestCase;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 12/19/13
 * Time: 3:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class OverridePropertyTest extends TestCase{

    public void testOverride(){
        SmcConfiguration.setOverriedProperty("smc.nsq.host", "192.168.1.100");

        System.out.println(AppConfiguration.getString("smc.nsq.host","").get());

        System.out.println(SmcConfiguration.environment);
    }
}
