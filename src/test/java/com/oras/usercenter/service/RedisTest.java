package com.oras.usercenter.service;

import com.oras.usercenter.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;

@SpringBootTest
public class RedisTest {

    @Resource
    private RedisTemplate redisTemplate;
    @Test
    void test(){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        // 增
        valueOperations.set("orasString", "fish");
        valueOperations.set("orasInt", 1);
        valueOperations.set("orasDouble", 2.0);
        User user = new User();
        user.setId(1L);
        user.setUsername("oras");
        valueOperations.set("orasUser", user);

        // 查
        Object shayu = valueOperations.get("orasString");
        Assertions.assertTrue("fish".equals((String) shayu));
        shayu = valueOperations.get("orasInt");
        Assertions.assertTrue(1 == (Integer) shayu);
        shayu = valueOperations.get("orasDouble");
        Assertions.assertTrue(2.0 == (Double) shayu);
        System.out.println(valueOperations.get("orasUser"));
        valueOperations.set("orasString", "fish");


    }
}
