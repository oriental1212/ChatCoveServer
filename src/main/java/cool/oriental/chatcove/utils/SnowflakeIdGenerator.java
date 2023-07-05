package cool.oriental.chatcove.utils;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author: Oriental
 * @Date: 2023-07-04-10:49
 * @Description: 雪花算法生成id
 */
public class SnowflakeIdGenerator {
    private final long twepoch = 1677472000000L; // 2021-01-01 00:00:00的时间戳
    private final long workerIdBits = 5L;
    private final long datacenterIdBits = 5L;
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);
    private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);
    private final long sequenceBits = 10L;
    private final long workerIdShift = sequenceBits;
    private final long datacenterIdShift = sequenceBits + workerIdBits;
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    private final long workerId;
    private final long datacenterId;
    private final AtomicLong sequence = new AtomicLong(0L);
    private long lastTimestamp = -1L;

    public SnowflakeIdGenerator(long workerId, long datacenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException("Worker ID超出范围");
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException("Datacenter ID超出范围");
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    public synchronized long nextId() {
        long timestamp = System.currentTimeMillis();

        if (timestamp < lastTimestamp) {
            throw new RuntimeException("时钟倒退异常");
        }

        if (timestamp == lastTimestamp) {
            long sequenceValue = sequence.getAndIncrement() & sequenceMask;
            if (sequenceValue == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
            return ((timestamp - twepoch) << timestampLeftShift)
                    | (datacenterId << datacenterIdShift)
                    | (workerId << workerIdShift)
                    | sequenceValue;
        } else {
            sequence.set(0L);
            lastTimestamp = timestamp;
            long sequenceValue = sequence.getAndIncrement() & sequenceMask;
            return ((timestamp - twepoch) << timestampLeftShift)
                    | (datacenterId << datacenterIdShift)
                    | (workerId << workerIdShift)
                    | sequenceValue;
        }
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }
}
