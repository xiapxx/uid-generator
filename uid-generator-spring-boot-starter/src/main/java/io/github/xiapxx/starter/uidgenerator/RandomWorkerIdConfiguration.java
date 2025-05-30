package io.github.xiapxx.starter.uidgenerator;

import io.github.xiapxx.starter.uidgenerator.properties.UidGeneratorProperties;
import io.github.xiapxx.starter.uidgenerator.worker.RandomWorkerIdAssigner;
import io.github.xiapxx.uid.generator.api.worker.WorkerIdAssigner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import static io.github.xiapxx.starter.uidgenerator.properties.UGWorkerConfig.PREFIX;

/**
 * @Author xiapeng
 * @Date 2025-05-30 14:30
 */
@ConditionalOnProperty(prefix = PREFIX, name = "type", havingValue = "random", matchIfMissing = true)
public class RandomWorkerIdConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public WorkerIdAssigner workerIdAssigner(UidGeneratorProperties uidGeneratorProperties) {
        return new RandomWorkerIdAssigner(uidGeneratorProperties.getWorker().getId());
    }


}
