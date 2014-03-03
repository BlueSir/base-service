package test.config;

import com.sohu.smc.config.service.SmcConfiguration;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.Iterator;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 11/15/13
 * Time: 11:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class PlaceHolderTest {
    public static void main(String[] args){
        SmcConfiguration.init();
        Resource resource = new ClassPathResource("context.xml");
        BeanFactory ctx = new XmlBeanFactory(resource);

        PropertyPlaceholderConfigurer configurer = new PropertyPlaceholderConfigurer();
        configurer.setSystemPropertiesMode(1);
        configurer.postProcessBeanFactory((XmlBeanFactory) ctx);

        PlaceHolder placeHolder = (PlaceHolder) ctx.getBean("placeHolder");
        Map<String, String> map = placeHolder.getSysProperty();
        Iterator<String> it = map.keySet().iterator();
        while(it.hasNext()){
            String key = it.next();
            System.out.println(key + " | "+ map.get(key));
        }

    }
}
