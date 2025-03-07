package io.github.xiapxx.starter.uidgenerator.worker;

import io.github.xiapxx.uid.generator.api.worker.WorkerIdAssigner;

/**
 * @Author xiapeng
 * @Date 2025-03-07 15:30
 */
public abstract class AbstractWorkerIdAssigner implements WorkerIdAssigner {

    private volatile Long currentWorkId;

    public final long assignWorkerId(long maxWorkerId) {
        if(currentWorkId != null){
            return currentWorkId;
        }
        currentWorkId = createWorkId(maxWorkerId);
        return currentWorkId;
    }

    public abstract long createWorkId(long maxWorkerId);

    protected Long getCurrentWorkId() {
        return currentWorkId;
    }
}
