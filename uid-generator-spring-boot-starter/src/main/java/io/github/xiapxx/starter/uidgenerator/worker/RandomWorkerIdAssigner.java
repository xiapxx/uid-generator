package io.github.xiapxx.starter.uidgenerator.worker;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机方式生成机器id(多应用实例时, 不保证生产的机器id不会重复)
 *
 * @Author xiapeng
 * @Date 2025-03-06 17:03
 */
public class RandomWorkerIdAssigner extends AbstractWorkerIdAssigner {

    private Long workerId;

    public RandomWorkerIdAssigner(Long workerId) {
        this.workerId = workerId;
    }

    @Override
    public long createWorkId(long maxWorkerId) {
        if(workerId != null){
            return workerId;
        }
        return ThreadLocalRandom.current().nextLong(1L, maxWorkerId);
    }

}
