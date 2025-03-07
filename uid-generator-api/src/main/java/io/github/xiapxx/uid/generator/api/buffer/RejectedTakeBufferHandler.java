package io.github.xiapxx.uid.generator.api.buffer;

/**
 * 拒绝策略: 当环已空, 无法继续获取时
 * 默认无需指定, 将记录日志, 并抛出UidGenerateException异常. 如有特殊需求, 请实现RejectedTakeBufferHandler接口
 */
@FunctionalInterface
public interface RejectedTakeBufferHandler {

    /**
     * Reject take buffer request
     * 
     * @param ringBuffer
     */
    void rejectTakeBuffer(RingBuffer ringBuffer);
}
