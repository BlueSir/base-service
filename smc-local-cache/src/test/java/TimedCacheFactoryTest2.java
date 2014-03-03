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
public class TimedCacheFactoryTest2 extends TestCase {


    @Override
    protected void setUp() throws Exception {
        Notify.init(7090);
    }
    /************** Server 2 *********************/
    public void testPutAndRemove() throws InterruptedException {
        TimedCacheFactory<String, String> timedCacheFactory = TimedCacheFactory.getInstance("test_local_cache", 10, 60 * 1000, "test_topic");
        timedCacheFactory.put("key", "value");
        String value = timedCacheFactory.get("key");
        Assert.assertEquals(value, "value");

        //Sleep 90秒
        TimeUnit.SECONDS.sleep(30);

        //Server 1 remove了key,此时 Server 2 的key也将被同步清除
        value = timedCacheFactory.get("key");
        Assert.assertNull(value);
    }

    /************** Server 2 *********************/
    public void testRemoveAndReload() throws InterruptedException {
        TimedCacheFactory<String, String> timedCacheFactory = TimedCacheFactory.getInstance("test_local_cache", 10, 60 * 1000, "test_topic", new RemoveCallBack<String, String>() {
            @Override
            public String getItem(String key) {
                return "newValue";
            }
        });
        timedCacheFactory.put("key", "value");
        String value = timedCacheFactory.get("key");
        Assert.assertEquals(value, "value");

        TimeUnit.SECONDS.sleep(30);
        //Server 1 remove了key,此时 Server 2 的key也将被同步清除并被重新load为 newValue
        value = timedCacheFactory.get("key");
        Assert.assertEquals(value, "newValue");
    }

    /************** Server 2 *********************/
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

        TimeUnit.SECONDS.sleep(30);

        //Server 1 refrushGroup("group"),此时 Server 2 的key也将被同步清除整个组
        value1 = groupInstance.get("key1");
        value2 = groupInstance.get("key2");
        value3 = groupInstance.get("key3");

        Assert.assertNull(value1);
        Assert.assertNull(value2);
        Assert.assertNull(value3);
    }

}
