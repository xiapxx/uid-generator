package io.github.xiapxx.uid.generator.api.buffer;

public interface RingBuffer {

    boolean put(long uid);

    long take();

}
