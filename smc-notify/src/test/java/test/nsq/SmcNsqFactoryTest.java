package test.nsq;

import com.nsq.ConsumerListener;
import com.nsq.Message;
import com.nsq.NSQMessage;
import com.nsq.exceptions.BadMessageException;
import com.nsq.exceptions.BadTopicException;
import com.nsq.exceptions.DisconnectedException;
import com.nsq.exceptions.NoConnectionsException;
import com.smc.notify.SmcNSQConsumer;
import com.smc.notify.SmcNSQFactory;
import com.smc.notify.SmcNSQProducer;
import com.sohu.smc.config.service.SmcConfiguration;
import junit.framework.TestCase;
import net.sf.json.JSONObject;

import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 12/23/13
 * Time: 4:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class SmcNsqFactoryTest {

    public static void main(String[] args) throws InterruptedException {
//        JSONObject jsonObject = JSONObject.fromObject("test");
        SmcConfiguration.setOverriedProperty("smc.nsq.test.lookup.host","127.0.0.1:4161");
        SmcConfiguration.setOverriedProperty("smc.nsq.test.producer.host","127.0.0.1:4150,127.0.0.1:5150");
        SmcNSQProducer producer = SmcNSQFactory.getProducer("test");

        Thread.sleep(1000);
        SmcNSQConsumer consumer = SmcNSQFactory.getConsumer("test");
        consumer.registerTopic("notify.test", new ConsumerListener() {
            public boolean excute(Message message) {
                System.out.println(message.toString());
                return true;
            }
        });
        for(int i=0;i<1000;i++){
            TimeUnit.MILLISECONDS.sleep(500);
            producer.produce("notify.test","test-"+i);
        }
    }
}
