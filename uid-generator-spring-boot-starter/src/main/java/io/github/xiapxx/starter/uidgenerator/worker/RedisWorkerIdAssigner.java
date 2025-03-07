package io.github.xiapxx.starter.uidgenerator.worker;

import io.github.xiapxx.uid.generator.api.worker.WorkerIdAssigner;
import org.springframework.data.redis.core.StringRedisTemplate;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * redis方式生成机器id
 * 1. 需保证每台机器都连接同一台redis;
 * 2. 保证每台机器的机器id不同
 *
 * @Author xiapeng
 * @Date 2025-03-06 17:21
 */
public class RedisWorkerIdAssigner implements WorkerIdAssigner, Runnable {

    private static final String REDIS_KEY = "uid_generator:";

    private static final int REDIS_TIME_OUT = 300;

    private static final int RENEW_INTERVAL = REDIS_TIME_OUT - 100;

    private StringRedisTemplate stringRedisTemplate;

    private Long currentWorkerId;

    private ScheduledThreadPoolExecutor scheduler;

    public RedisWorkerIdAssigner(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 分配机器id(启动阶段)
     *
     * @param maxWorkerId maxWorkerId
     * @return 机器id
     */
    @Override
    public long assignWorkerId(long maxWorkerId) {
        if(currentWorkerId != null){
            return currentWorkerId;
        }

        Set<Long> usingWorkerIdSet = getUsingWorkerId();
        for (long i = 1; i < maxWorkerId; i++) {
            if(usingWorkerIdSet.contains(i)){
                continue;
            }
            String redisKey = REDIS_KEY + ":" + i;
            if(stringRedisTemplate.opsForValue().setIfAbsent(redisKey, "机器ip", REDIS_TIME_OUT, TimeUnit.SECONDS)){
                currentWorkerId = i;
                startRenewWorkerId();
                return i;
            }
        }
        throw new IllegalArgumentException("Unable to allocate Worker ID");
    }

    /**
     * 启动续约机器id
     */
    private void startRenewWorkerId() {
        scheduler = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);
        scheduler.scheduleWithFixedDelay(this, 10, RENEW_INTERVAL, TimeUnit.SECONDS);
    }

    /**
     * 获取使用中的机器id集合
     *
     * @return 使用中的机器id
     */
    private Set<Long> getUsingWorkerId() {
        Set<String> keys = stringRedisTemplate.keys(REDIS_KEY + "*");
        if(keys == null || keys.isEmpty()){
            return new HashSet<>();
        }
        return keys.stream().map(item -> Long.valueOf(item)).collect(Collectors.toSet());
    }

    /**
     * 续约当前分配的机器id
     */
    @Override
    public void run() {
        stringRedisTemplate.expire(REDIS_KEY + ":" + currentWorkerId, REDIS_TIME_OUT, TimeUnit.SECONDS);
    }
}
