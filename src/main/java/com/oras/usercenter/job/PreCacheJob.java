package com.oras.usercenter.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oras.usercenter.mapper.UserMapper;
import com.oras.usercenter.model.domain.User;
import com.oras.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

/**
 * 缓存预热任务
 */
@Component
@Slf4j
public class PreCacheJob {

    @Resource
    private UserService userService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private RedissonClient redissonClient;


    //重点用户
    private List<Long> mainUserList = Arrays.asList(1L);

    //每天执行，预热推荐用户
    @Scheduled(cron = "0 50 23 * * *")
    public void doCacheCommendUser(){

        RLock lock = redissonClient.getLock("teamup:precachejob:docache:lock");
        try{
            //  唯一线程可获取锁
            if( lock.tryLock(0,-1,TimeUnit.MILLISECONDS) ){
                System.out.println("getLock: " + Thread.currentThread().getId());
                for (Long userId : mainUserList){

                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    Page<User> userPage = userService.page(new Page<>(1, 20), queryWrapper);

                    String redisKey = String.format("teamup:user:recommend:%s", userId);
                    ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
                    //写缓存 30s过期
                    try {
                        valueOperations.set(redisKey,userPage,30000, TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        log.error("redis set key error ", e );
                    }

                }
            }


        }catch (InterruptedException e){
            System.out.println("unLock: " + Thread.currentThread().getId());
            log.error("doCacheCommendUser error",e);
        }finally {
            //线程只能释放自己的锁
            if(lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }


    }


}
