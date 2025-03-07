package io.github.xiapxx.uid.generator.api.worker;

public interface WorkerIdAssigner {

    long assignWorkerId(long maxWorkerId);

}
