package io.github.xiapxx.starter.uidgenerator;

import io.github.xiapxx.starter.uidgenerator.worker.RedisWorkerIdAssigner;
import io.github.xiapxx.uid.generator.api.worker.WorkerIdAssigner;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import static io.github.xiapxx.starter.uidgenerator.properties.UGWorkerConfig.PREFIX;

/**
 * @Author xiapeng
 * @Date 2025-03-06 17:11
 */
@ConditionalOnClass({StringRedisTemplate.class, RedisAutoConfiguration.class})
@AutoConfigureAfter(name = {"org.redisson.spring.starter.RedissonAutoConfiguration",
        "org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration"}
)
@ConditionalOnProperty(prefix = PREFIX, name = "type", havingValue = "redis", matchIfMissing = true)
public class RedisWorkerIdConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public WorkerIdAssigner workerIdAssigner(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(redisConnectionFactory);
        stringRedisTemplate.afterPropertiesSet();
        return new RedisWorkerIdAssigner(stringRedisTemplate);
    }

}
