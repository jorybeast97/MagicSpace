package space.wujiu.redishandler.redisMigration;


import org.springframework.stereotype.Component;

@Component
public class RedisCacheMoveLogic {

    public void setRedisKey(String key,String value) {
        System.out.println("向Redis 2中添加数据,KEY = " + key + " VALUE = " + value);
    }

    public void getRedisKey() {
        System.out.println("从Redis 2中获取数据");
    }
}
