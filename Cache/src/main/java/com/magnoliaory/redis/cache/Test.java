package com.magnoliaory.redis.cache;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class Test {

    @Cacheable(cacheNames = "objectCache",keyGenerator = "myKeyGenerator")
    public Object selectDemo(Integer arg) {
        //
        return new Object();
    }
}
