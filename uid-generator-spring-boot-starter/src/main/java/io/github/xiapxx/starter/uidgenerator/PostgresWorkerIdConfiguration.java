package io.github.xiapxx.starter.uidgenerator;

import io.github.xiapxx.starter.uidgenerator.properties.UidGeneratorProperties;
import io.github.xiapxx.starter.uidgenerator.worker.PostgresWorkerIdAssigner;
import io.github.xiapxx.uid.generator.api.worker.WorkerIdAssigner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import static io.github.xiapxx.starter.uidgenerator.properties.UGWorkerConfig.PREFIX;

/**
 * @Author xiapeng
 * @Date 2025-03-07 14:20
 */
@ConditionalOnProperty(prefix = PREFIX, name = "type", havingValue = "postgres")
public class PostgresWorkerIdConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public WorkerIdAssigner workerIdAssigner(UidGeneratorProperties uidGeneratorProperties) {
        return new PostgresWorkerIdAssigner(uidGeneratorProperties.getWorker().getDataSource());
    }


}
