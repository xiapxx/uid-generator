package io.github.xiapxx.uid.generator.api.buffer;

/**
 * 拒绝策略: 当环已满, 无法继续填充时
 * 默认无需指定, 将丢弃Put操作, 默认无操作. 如有特殊需求, 请实现RejectedPutBufferHandler接口
 */
@FunctionalInterface
public interface RejectedPutBufferHandler {

    /**
     * Reject put buffer request
     *
     * @param ringBuffer
     * @param uid
     */
    void rejectPutBuffer(RingBuffer ringBuffer, long uid);
}
