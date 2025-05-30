package io.github.xiapxx.starter.uidgenerator;

import io.github.xiapxx.starter.uidgenerator.holder.UidGeneratorHolder;
import io.github.xiapxx.starter.uidgenerator.properties.UidGeneratorProperties;
import io.github.xiapxx.uid.generator.api.buffer.RejectedPutBufferHandler;
import io.github.xiapxx.uid.generator.api.buffer.RejectedTakeBufferHandler;
import io.github.xiapxx.uid.generator.api.worker.WorkerIdAssigner;
import io.github.xiapxx.uid.generator.api.UidGenerator;
import io.github.xiapxx.uid.generator.impl.core.CachedUidGenerator;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * 基于baidu的UidGenerator;
 * 1. 性能高;
 * 2. 解决了时间回退问题
 *
 * @Author xiapeng
 * @Date 2025-03-06 15:50
 */
@EnableConfigurationProperties(UidGeneratorProperties.class)
@Import({
        RedisWorkerIdConfiguration.class,
        PostgresWorkerIdConfiguration.class,
        MysqlWorkerIdConfiguration.class,
        RandomWorkerIdConfiguration.class
})
public class UidGeneratorAutoConfiguration {


    @Bean(initMethod = "init", destroyMethod = "destroy")
    public UidGenerator uidGenerator(WorkerIdAssigner workerIdAssigner,
                                     ObjectProvider<RejectedPutBufferHandler> rejectedPutBufferHandlerObjectProvider,
                                     ObjectProvider<RejectedTakeBufferHandler> rejectedTakeBufferHandlerObjectProvider,
                                     UidGeneratorProperties uidGeneratorProperties) {
        CachedUidGenerator cachedUidGenerator = new CachedUidGenerator();
        cachedUidGenerator.setBoostPower(uidGeneratorProperties.getBoostPower());
        cachedUidGenerator.setTimeBits(uidGeneratorProperties.getTimeBits());
        cachedUidGenerator.setWorkerBits(uidGeneratorProperties.getWorkerBits());
        cachedUidGenerator.setSeqBits(uidGeneratorProperties.getSeqBits());
        cachedUidGenerator.setEpochStr(uidGeneratorProperties.getEpochStr());
        cachedUidGenerator.setWorkerIdAssigner(workerIdAssigner);
        cachedUidGenerator.setPaddingFactor(uidGeneratorProperties.getPaddingFactor());

        if(uidGeneratorProperties.getScheduleInterval() != null){
            cachedUidGenerator.setScheduleInterval(uidGeneratorProperties.getScheduleInterval());
        }

        RejectedPutBufferHandler rejectedPutBufferHandler = rejectedPutBufferHandlerObjectProvider.getIfAvailable();
        if(rejectedPutBufferHandler != null){
            cachedUidGenerator.setRejectedPutBufferHandler(rejectedPutBufferHandler);
        }

        RejectedTakeBufferHandler rejectedTakeBufferHandler = rejectedTakeBufferHandlerObjectProvider.getIfAvailable();
        if(rejectedTakeBufferHandler != null){
            cachedUidGenerator.setRejectedTakeBufferHandler(rejectedTakeBufferHandler);
        }

        return cachedUidGenerator;
    }

    @Bean
    public UidGeneratorHolder uidGeneratorHolder(UidGenerator uidGenerator) {
        return new UidGeneratorHolder(uidGenerator);
    }

}
