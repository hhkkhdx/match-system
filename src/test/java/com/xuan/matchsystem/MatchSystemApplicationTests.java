package com.xuan.matchsystem;

import io.lettuce.core.RedisClient;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class MatchSystemApplicationTests {

    @Autowired
    private RedissonClient redissonClient;

    @Test
    void contextLoads() {

    }

}
