package test.config;

import com.netflix.config.DynamicStringProperty;
import com.sohu.smc.config.model.AppConfiguration;
import com.sohu.smc.config.service.SmcConfiguration;
import junit.framework.TestCase;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 11/12/13
 * Time: 10:28 AM
 * To change this template use File | Settings | File Templates.
 */
public class AppConfigurationTest extends TestCase{

    public void testGetString() throws Exception {

        //从配置中心获取Key=smc.test.property的值, 此时该配置值为: property
        DynamicStringProperty property = AppConfiguration.getString("smc.test.property", "", new Runnable() {
            @Override
            public void run() {
                System.out.println("Runnable.run");
            }
        });

        //打印的值为：property=property
        System.out.println("property=" + property.get());

        Thread.sleep(60000);
        //打开配置中心的配置页面:http://10.13.80.133:8030/base/config.jsp 修改smc.test.property的值为：modify-property
        //此时打印的值为：property=modify-property
        System.out.println(property.get());



    }
}
