package io.github.xiapxx.starter.uidgenerator.worker;

import org.springframework.data.redis.core.StringRedisTemplate;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * redis方式生成机器id
 * 1. 需保证每台机器都连接同一台redis;
 * 2. 保证每台机器的机器id不同
 *
 * @Author xiapeng
 * @Date 2025-03-06 17:21
 */
public class RedisWorkerIdAssigner extends AbstractWorkerIdAssigner implements Runnable {

    private static final String REDIS_KEY = "uid_generator:";

    private static final int REDIS_TIME_OUT = 300;

    private static final int RENEW_INTERVAL = REDIS_TIME_OUT - 100;

    private StringRedisTemplate stringRedisTemplate;

    private String currentIp;

    private ScheduledThreadPoolExecutor scheduler;

    public RedisWorkerIdAssigner(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.currentIp = getLocalIp();
    }

    private String getLocalIp() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            return localHost.getHostAddress();
        } catch (UnknownHostException e) {
            return "UnknownHost";
        }
    }

    /**
     * 分配机器id(启动阶段)
     *
     * @param maxWorkerId maxWorkerId
     * @return 机器id
     */
    @Override
    public long createWorkId(long maxWorkerId) {
        Set<String> usingAppRedisKeys = getUsingAppRedisKeys();
        for (long i = 1; i < maxWorkerId; i++) {
            String appRedisKey = getAppRedisKey(i);
            if(usingAppRedisKeys.contains(appRedisKey)){
                continue;
            }

            if(stringRedisTemplate.opsForValue().setIfAbsent(appRedisKey, currentIp)){
                stringRedisTemplate.expire(appRedisKey, REDIS_TIME_OUT, TimeUnit.SECONDS);
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
     * 获取使用中的应用redis key
     *
     * @return 使用中的应用redis key
     */
    private Set<String> getUsingAppRedisKeys() {
        Set<String> keys = stringRedisTemplate.keys(REDIS_KEY + "*");
        return keys == null ? new HashSet<>() : keys;
    }

    /**
     * 获取当前应用的redis key
     *
     * @param index index
     * @return 应用的redis key
     */
    private String getAppRedisKey(long index) {
        return REDIS_KEY + index;
    }

    /**
     * 续约当前分配的机器id
     */
    @Override
    public void run() {
        Long currentWorkId = getCurrentWorkId();
        if(currentWorkId == null){
            return;
        }
        stringRedisTemplate.expire(getAppRedisKey(currentWorkId), REDIS_TIME_OUT, TimeUnit.SECONDS);
    }
}
