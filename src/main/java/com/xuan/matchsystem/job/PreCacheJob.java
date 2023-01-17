package com.xuan.matchsystem.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuan.matchsystem.model.domain.User;
import com.xuan.matchsystem.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @description: 缓存预热
 * @author: xuan
 * @date: 2023/1/8 16:42
 **/
//@Component
@Slf4j
public class PreCacheJob {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    // 重点用户
    private List<Long> mainUserList = Arrays.asList(1L);

    @Scheduled(cron = "")
    public void doCacheRecommendUser() {
        RLock lock = redissonClient.getLock("matchsystem:precachejob:docache:lock");
        try {
            if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                for (Long userId: mainUserList) {
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    Page<User> userPage = userService.page(new Page<>(1, 20), queryWrapper);
                    String key = String.format("matchsystem:user:recommend:%s", userId);
                    ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
                    try {
                        opsForValue.set(key, userPage, 10, TimeUnit.MINUTES);
                    } catch (Exception e) {
                        log.error("redis set error: ", e);
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("cache set error", e);
        } finally {
            // 释放锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

    }


}
