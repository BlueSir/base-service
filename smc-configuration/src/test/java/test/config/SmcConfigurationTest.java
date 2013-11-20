package test.config;

import com.sohu.smc.config.exception.SmcConfigurationException;
import com.sohu.smc.config.service.SmcConfiguration;
import junit.framework.TestCase;

import java.util.Iterator;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 11/12/13
 * Time: 4:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class SmcConfigurationTest extends TestCase{
    public void testList() throws InterruptedException, SmcConfigurationException {
        SmcConfiguration.init();
//        SmcConfiguration.setZkProperty("smc.api.test1", "value1");
//        SmcConfiguration.setZkProperty("smc.api.test2", "value2");
//        SmcConfiguration.setZkProperty("smc.api.test3", "value3");
//        SmcConfiguration.setZkProperty("smc.user.test3", "value3");
//        SmcConfiguration.setZkProperty("smc.user.test3", "value3");
        Thread.sleep(2000);
        Map<String, Map<String,Object>> properties = SmcConfiguration.properties();
        Iterator<String> prefixIt = properties.keySet().iterator();
        while(prefixIt.hasNext()){
            String prefix = prefixIt.next();

            Map<String, Object> values = properties.get(prefix);
            Iterator<String> valuesIt = values.keySet().iterator();
            while(valuesIt.hasNext()){
                String key = valuesIt.next();
                Object value = values.get(key);
                System.out.println(key + " | "+ value);
                String modifyScript = "<a href=\"modifyConfig.jsp?act=show&key=" + key +"&value=" + value +"\">修改</a>";
                String delScript = "<a href=\"config.jsp?act=del&key=" + key +"\">删除</a>";
            }
        }
    }

    public static void main(String[] args){
        for(int i=0;i<100;i++){
          new MyThread(i).start();
        }
    }
}

class MyThread extends Thread{
    public int index;
    public MyThread(int index){
        this.index = index;
    }
    @Override
    public void run() {
        System.out.println("Thread-"+index + "@");
        SmcConfiguration.init();
    }
}