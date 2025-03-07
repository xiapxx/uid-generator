package io.github.xiapxx.starter.uidgenerator.worker;

import io.github.xiapxx.uid.generator.api.worker.WorkerIdAssigner;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机方式生成机器id(多应用实例时, 不保证生产的机器id不会重复)
 *
 * @Author xiapeng
 * @Date 2025-03-06 17:03
 */
public class RandomWorkerIdAssigner implements WorkerIdAssigner {

    private Long workerId;

    public RandomWorkerIdAssigner(Long workerId) {
        this.workerId = workerId;
    }

    @Override
    public long assignWorkerId(long maxWorkerId) {
        if(workerId != null){
            return workerId;
        }
        return ThreadLocalRandom.current().nextLong(1L, maxWorkerId);
    }

}
