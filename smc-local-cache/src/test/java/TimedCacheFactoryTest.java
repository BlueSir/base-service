import com.smc.local.cache.CacheGroup;
import com.smc.local.cache.RemoveCallBack;
import com.smc.local.cache.TimedCacheFactory;
import com.smc.notify.Notify;
import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 11/28/13
 * Time: 11:44 AM
 * To change this template use File | Settings | File Templates.
 */
public class TimedCacheFactoryTest extends TestCase {

    @Override
    protected void setUp() throws Exception {
        Notify.init(7080);
    }

    /************** Server 1 *********************/
    public void testPutAndRemove() throws InterruptedException {
        TimedCacheFactory<String, String> timedCacheFactory = TimedCacheFactory.getInstance("test_local_cache", 10, 60 * 1000, "test_topic");
        timedCacheFactory.put("key", "value");
        String value = timedCacheFactory.get("key");
        Assert.assertEquals(value, "value");

        //Sleep 10 秒
        TimeUnit.SECONDS.sleep(10);
        timedCacheFactory.remove("key");
        value = timedCacheFactory.get("key");
        Assert.assertNull(value);

    }

    /************** Server 1 *********************/
    public void testRemoveAndReload() throws InterruptedException {
        TimedCacheFactory<String, String> timedCacheFactory = TimedCacheFactory.getInstance("test_local_cache", 10, 5 * 60 * 1000, "test_topic", new RemoveCallBack<String,String>() {
            @Override
            public String getItem(String key) {
                return "newValue";
            }
        });
        timedCacheFactory.put("key", "value");
        String value = timedCacheFactory.get("key");
        Assert.assertEquals(value, "value");

        //Sleep 30秒
        TimeUnit.SECONDS.sleep(10);
        timedCacheFactory.remove("key");

        TimeUnit.SECONDS.sleep(2);
        value = timedCacheFactory.get("key");
        Assert.assertEquals(value, "newValue");
    }


    /************** Server 1 *********************/
    public void testGroup() throws InterruptedException {
        TimedCacheFactory<String, String> groupInstance = TimedCacheFactory.getGroupInstance("test_local_group_cache", 10, 60 * 1000, "test_topic");
        CacheGroup group = groupInstance.getGroup("group");
        groupInstance.put("key1", "value1", group);
        groupInstance.put("key2", "value2", group);
        groupInstance.put("key3", "value3", group);

        String value1 = groupInstance.get("key1");
        String value2 = groupInstance.get("key2");
        String value3 = groupInstance.get("key3");

        Assert.assertEquals(value1, "value1");
        Assert.assertEquals(value2, "value2");
        Assert.assertEquals(value3, "value3");

        TimeUnit.SECONDS.sleep(10);

        groupInstance.refrushGroup("group");

        value1 = groupInstance.get("key1");
        value2 = groupInstance.get("key2");
        value3 = groupInstance.get("key3");

        Assert.assertNull(value1);
        Assert.assertNull(value2);
        Assert.assertNull(value3);
    }

}
