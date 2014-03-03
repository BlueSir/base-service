import com.sohu.smc.redis.SmcJedis;
import com.sohu.smc.redis.SmcJedisFactory;
import junit.framework.TestCase;

import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 11/30/13
 * Time: 4:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class JedisFactoryTest extends TestCase {
    public void testPlaceHolder() throws InterruptedException {
        SmcJedis jedis = SmcJedisFactory.getInstance("counter");

        jedis.hset("user_jingxc2", "username", "jingxc2");
        jedis.hset("user_jingxc2", "password", "123456");
        jedis.hset("user_jingxc2", "name", "Jing Xiaocai");

        String userName = jedis.hget("user_jingxc2", "username");
        String password = jedis.hget("user_jingxc2", "password");
        String name = jedis.hget("user_jingxc2", "name");

        System.out.println(name + " | " + password + " | " + name);
    }
}
