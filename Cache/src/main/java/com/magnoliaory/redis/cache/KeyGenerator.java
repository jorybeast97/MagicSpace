package com.magnoliaory.redis.cache;


import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.UUID;

@Component
public class KeyGenerator {

    @Bean(name = "myKeyGenerator")
    public org.springframework.cache.interceptor.KeyGenerator getMyKey() {
        return new org.springframework.cache.interceptor.KeyGenerator() {
            @Override
            public Object generate(Object o, Method method, Object... objects) {
                if (objects.length == 0) {
                    return UUID.randomUUID().toString();
                }
                String key = objects[0].toString();
                return key;
            }
        };
    }
}
