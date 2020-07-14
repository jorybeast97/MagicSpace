package space.wujiu.redishandler.cache;

import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import space.wujiu.redishandler.redisMigration.RedisLogicHandler;


@Component
@RedisLogicHandler
public class RedisCacheLogic {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public void setRedisKey(String key,String value) {
        redisTemplate.opsForValue().set(key, value);
        System.out.println("向Redis 1中添加数据,KEY = " + key + " VALUE = " + value);
    }

    public void getRedisKey(String key) {
        System.out.println("从Redis1中获取数据 " + redisTemplate.opsForValue().get(key));
    }

}
