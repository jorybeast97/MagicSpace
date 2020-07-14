package space.wujiu.redishandler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import space.wujiu.redishandler.cache.RedisCacheLogic;

@SpringBootTest
class RedisHandlerApplicationTests {

    @Autowired
    RedisCacheLogic cacheLogic;


    @Test
    void contextLoads() {
        cacheLogic.getRedisKey("EASK120183A");
    }

}
