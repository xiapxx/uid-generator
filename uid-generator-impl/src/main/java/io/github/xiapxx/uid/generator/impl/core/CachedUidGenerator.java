package io.github.xiapxx.uid.generator.impl.core;

import io.github.xiapxx.uid.generator.api.buffer.RejectedPutBufferHandler;
import io.github.xiapxx.uid.generator.api.buffer.RejectedTakeBufferHandler;
import io.github.xiapxx.uid.generator.impl.buffer.BufferPaddingExecutor;
import io.github.xiapxx.uid.generator.impl.buffer.RingBufferImpl;
import io.github.xiapxx.uid.generator.api.exception.UidGenerateException;
import io.github.xiapxx.uid.generator.impl.utils.UGAssertUtils;
import io.github.xiapxx.uid.generator.impl.BitsAllocator;
import java.util.ArrayList;
import java.util.List;

public class CachedUidGenerator extends DefaultUidGenerator {
    private static final int DEFAULT_BOOST_POWER = 3;

    /** Spring properties */
    private int boostPower = DEFAULT_BOOST_POWER;
    private int paddingFactor = RingBufferImpl.DEFAULT_PADDING_PERCENT;
    private Long scheduleInterval;
    
    private RejectedPutBufferHandler rejectedPutBufferHandler;
    private RejectedTakeBufferHandler rejectedTakeBufferHandler;

    /** RingBuffer */
    private RingBufferImpl ringBuffer;
    private BufferPaddingExecutor bufferPaddingExecutor;

    @Override
    public void init() throws Exception {
        // initialize workerId & bitsAllocator
        super.init();
        
        // initialize RingBuffer & RingBufferPaddingExecutor
        this.initRingBuffer();
    }
    
    @Override
    public long getUID() {
        try {
            return ringBuffer.take();
        } catch (Exception e) {
            throw new UidGenerateException(e);
        }
    }

    @Override
    public String parseUID(long uid) {
        return super.parseUID(uid);
    }
    
    public void destroy() {
        bufferPaddingExecutor.shutdown();
    }

    /**
     * Get the UIDs in the same specified second under the max sequence
     * 
     * @param currentSecond
     * @return UID list, size of {@link BitsAllocator#getMaxSequence()} + 1
     */
    protected List<Long> nextIdsForOneSecond(long currentSecond) {
        // Initialize result list size of (max sequence + 1)
        int listSize = (int) bitsAllocator.getMaxSequence() + 1;
        List<Long> uidList = new ArrayList<>(listSize);

        // Allocate the first sequence of the second, the others can be calculated with the offset
        long firstSeqUid = bitsAllocator.allocate(currentSecond - epochSeconds, workerId, 0L);
        for (int offset = 0; offset < listSize; offset++) {
            uidList.add(firstSeqUid + offset);
        }

        return uidList;
    }
    
    /**
     * Initialize RingBuffer & RingBufferPaddingExecutor
     */
    private void initRingBuffer() {
        // initialize RingBuffer
        int bufferSize = ((int) bitsAllocator.getMaxSequence() + 1) << boostPower;
        this.ringBuffer = new RingBufferImpl(bufferSize, paddingFactor);

        // initialize RingBufferPaddingExecutor
        boolean usingSchedule = (scheduleInterval != null);
        this.bufferPaddingExecutor = new BufferPaddingExecutor(ringBuffer, this::nextIdsForOneSecond, usingSchedule);
        if (usingSchedule) {
            bufferPaddingExecutor.setScheduleInterval(scheduleInterval);
        }

        // set rejected put/take handle policy
        this.ringBuffer.setBufferPaddingExecutor(bufferPaddingExecutor);
        if (rejectedPutBufferHandler != null) {
            this.ringBuffer.setRejectedPutHandler(rejectedPutBufferHandler);
        }
        if (rejectedTakeBufferHandler != null) {
            this.ringBuffer.setRejectedTakeHandler(rejectedTakeBufferHandler);
        }
        
        // fill in all slots of the RingBuffer
        bufferPaddingExecutor.paddingBuffer();
        
        // start buffer padding threads
        bufferPaddingExecutor.start();
    }

    /**
     * Setters for spring property
     */
    public void setBoostPower(int boostPower) {
        UGAssertUtils.isTrue(boostPower > 0, "Boost power must be positive!");
        this.boostPower = boostPower;
    }
    
    public void setRejectedPutBufferHandler(RejectedPutBufferHandler rejectedPutBufferHandler) {
        UGAssertUtils.notNull(rejectedPutBufferHandler, "RejectedPutBufferHandler can't be null!");
        this.rejectedPutBufferHandler = rejectedPutBufferHandler;
    }

    public void setRejectedTakeBufferHandler(RejectedTakeBufferHandler rejectedTakeBufferHandler) {
        UGAssertUtils.notNull(rejectedTakeBufferHandler, "RejectedTakeBufferHandler can't be null!");
        this.rejectedTakeBufferHandler = rejectedTakeBufferHandler;
    }

    public void setScheduleInterval(long scheduleInterval) {
        UGAssertUtils.isTrue(scheduleInterval > 0, "Schedule interval must positive!");
        this.scheduleInterval = scheduleInterval;
    }

    public void setPaddingFactor(int paddingFactor) {
        UGAssertUtils.isTrue(paddingFactor > 0, "paddingFactor must positive!");
        this.paddingFactor = paddingFactor;
    }
}
