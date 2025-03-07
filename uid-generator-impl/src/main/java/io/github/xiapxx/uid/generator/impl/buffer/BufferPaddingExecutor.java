package io.github.xiapxx.uid.generator.impl.buffer;

import io.github.xiapxx.uid.generator.api.buffer.RingBuffer;
import io.github.xiapxx.uid.generator.impl.utils.PaddedAtomicLong;
import io.github.xiapxx.uid.generator.impl.utils.UGAssertUtils;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Represents an executor for padding {@link RingBuffer}<br>
 * There are two kinds of executors: one for scheduled padding, the other for padding immediately.
 * 
 * @author yutianbao
 */
public class BufferPaddingExecutor {

    /** Constants */
    private static final long DEFAULT_SCHEDULE_INTERVAL = 5 * 60L; // 5 minutes
    
    /** Whether buffer padding is running */
    private final AtomicBoolean running;

    /** We can borrow UIDs from the future, here store the last second we have consumed */
    private final PaddedAtomicLong lastSecond;

    /** RingBuffer & BufferUidProvider */
    private final RingBuffer ringBuffer;
    private final BufferedUidProvider uidProvider;

    /** Padding immediately by the thread pool */
    private final ExecutorService bufferPadExecutors;
    /** Padding schedule thread */
    private final ScheduledExecutorService bufferPadSchedule;
    
    /** Schedule interval Unit as seconds */
    private long scheduleInterval = DEFAULT_SCHEDULE_INTERVAL;

    /**
     * Constructor with {@link RingBuffer} and {@link BufferedUidProvider}, default use schedule
     *
     * @param ringBuffer {@link RingBuffer}
     * @param uidProvider {@link BufferedUidProvider}
     */
    public BufferPaddingExecutor(RingBuffer ringBuffer, BufferedUidProvider uidProvider) {
        this(ringBuffer, uidProvider, true);
    }

    /**
     * Constructor with {@link RingBuffer}, {@link BufferedUidProvider}, and whether use schedule padding
     *
     * @param ringBuffer {@link RingBuffer}
     * @param uidProvider {@link BufferedUidProvider}
     * @param usingSchedule
     */
    public BufferPaddingExecutor(RingBuffer ringBuffer, BufferedUidProvider uidProvider, boolean usingSchedule) {
        this.running = new AtomicBoolean(false);
        this.lastSecond = new PaddedAtomicLong(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
        this.ringBuffer = ringBuffer;
        this.uidProvider = uidProvider;

        // initialize thread pool
        int cores = Runtime.getRuntime().availableProcessors();
        bufferPadExecutors = Executors.newFixedThreadPool(cores * 2);

        // initialize schedule thread
        if (usingSchedule) {
            bufferPadSchedule = Executors.newSingleThreadScheduledExecutor();
        } else {
            bufferPadSchedule = null;
        }
    }

    /**
     * Start executors such as schedule
     */
    public void start() {
        if (bufferPadSchedule != null) {
            bufferPadSchedule.scheduleWithFixedDelay(() -> paddingBuffer(), scheduleInterval, scheduleInterval, TimeUnit.SECONDS);
        }
    }

    /**
     * Shutdown executors
     */
    public void shutdown() {
        if (!bufferPadExecutors.isShutdown()) {
            bufferPadExecutors.shutdownNow();
        }

        if (bufferPadSchedule != null && !bufferPadSchedule.isShutdown()) {
            bufferPadSchedule.shutdownNow();
        }
    }

    /**
     * Whether is padding
     *
     * @return
     */
    public boolean isRunning() {
        return running.get();
    }

    /**
     * Padding buffer in the thread pool
     */
    public void asyncPadding() {
        bufferPadExecutors.submit(this::paddingBuffer);
    }

    /**
     * Padding buffer fill the slots until to catch the cursor
     */
    public void paddingBuffer() {

        // is still running
        if (!running.compareAndSet(false, true)) {
            return;
        }

        // fill the rest slots until to catch the cursor
        boolean isFullRingBuffer = false;
        while (!isFullRingBuffer) {
            List<Long> uidList = uidProvider.provide(lastSecond.incrementAndGet());
            for (Long uid : uidList) {
                isFullRingBuffer = !ringBuffer.put(uid);
                if (isFullRingBuffer) {
                    break;
                }
            }
        }

        // not running now
        running.compareAndSet(true, false);
    }

    /**
     * Setters
     */
    public void setScheduleInterval(long scheduleInterval) {
        UGAssertUtils.isTrue(scheduleInterval > 0, "Schedule interval must positive!");
        this.scheduleInterval = scheduleInterval;
    }
    
}
