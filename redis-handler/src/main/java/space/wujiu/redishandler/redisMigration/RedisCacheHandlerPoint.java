package space.wujiu.redishandler.redisMigration;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;


@Component
@Aspect
public class RedisCacheHandlerPoint {



    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RedisCacheMoveLogic redisCacheMoveLogic;

    @After("execution(* space.wujiu.redishandler.cache..RedisCacheLogic.*(..))")
    public void redisHandler(JoinPoint joinPoint) {
        Object[] paramValues = joinPoint.getArgs();
        String methodName = ((MethodSignature) joinPoint.getSignature()).getName();
        if (methodName.equals("setRedisKey")) {
            redisCacheMoveLogic.setRedisKey((String) paramValues[0], (String) paramValues[1]);
        }else {
            logger.warn("redisHandler - "+methodName+"方法未在增强行列中");
        }
    }



    /**
     * 从切面类中获取相关参数
     * @param joinPoint
     * @return
     */
    public Map<String, Object> getMethodParamInfo(JoinPoint joinPoint) {
        final Map<String,Object> methodInfo = new LinkedHashMap<>();
        Object[] paramValues = joinPoint.getArgs();
        String[] paramNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
        for (int i = 0; i < paramValues.length; i++) {
            methodInfo.put(paramNames[i], paramValues[i]);
        }
        return methodInfo;
    }


    public void printMap(Map<String,Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            System.out.println("参数为 : " + entry.getKey() + "   " + entry.getValue());
        }
    }



}
