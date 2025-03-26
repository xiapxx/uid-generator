package io.github.xiapxx.starter.uidgenerator.properties;

import io.github.xiapxx.starter.uidgenerator.enums.UGWorkerTypeEnum;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.sql.Driver;
import java.util.ServiceLoader;
import static io.github.xiapxx.starter.uidgenerator.properties.UidGeneratorProperties.PREFIX;

/**
 * @Author xiapeng
 * @Date 2025-03-06 16:00
 */
@ConfigurationProperties(prefix = PREFIX)
public class UidGeneratorProperties implements InitializingBean {

    public static final String PREFIX = "uid.generator";

    private int timeBits = 29;

    private int workerBits = 21;

    private int seqBits = 13;

    /**
     * 时间基点; 最多可支持约8.7年
     *
     * 格式: yyyy-MM-dd
     */
    private String epochStr = "2025-03-07";

    /**
     * RingBuffer size扩容参数, 可提高UID生成的吞吐量
     * 默认:3， 原bufferSize=8192, 扩容后bufferSize= 8192 << 3 = 65536
     */
    private int boostPower = 3;

    /**
     * 指定何时向RingBuffer中填充UID, 取值为百分比(0, 100), 默认为50
     * 举例: bufferSize=1024, paddingFactor=50 -> threshold=1024 * 50 / 100 = 512
     * 当环上可用UID数量 < 512时, 将自动对RingBuffer进行填充补全
     */
    private int paddingFactor = 50;

    /**
     * 另外一种RingBuffer填充时机, 在Schedule线程中, 周期性检查填充
     * 默认:不配置此项, 即不实用Schedule线程. 如需使用, 请指定Schedule线程时间间隔, 单位:秒
     */
    private Long scheduleInterval;

    /**
     * 生成worker id的相关配置
     */
    private UGWorkerConfig worker = new UGWorkerConfig();

    public int getTimeBits() {
        return timeBits;
    }

    public void setTimeBits(int timeBits) {
        this.timeBits = timeBits;
    }

    public int getWorkerBits() {
        return workerBits;
    }

    public void setWorkerBits(int workerBits) {
        this.workerBits = workerBits;
    }

    public int getSeqBits() {
        return seqBits;
    }

    public void setSeqBits(int seqBits) {
        this.seqBits = seqBits;
    }

    public String getEpochStr() {
        return epochStr;
    }

    public void setEpochStr(String epochStr) {
        this.epochStr = epochStr;
    }

    public int getBoostPower() {
        return boostPower;
    }

    public void setBoostPower(int boostPower) {
        this.boostPower = boostPower;
    }

    public int getPaddingFactor() {
        return paddingFactor;
    }

    public void setPaddingFactor(int paddingFactor) {
        this.paddingFactor = paddingFactor;
    }

    public Long getScheduleInterval() {
        return scheduleInterval;
    }

    public void setScheduleInterval(Long scheduleInterval) {
        this.scheduleInterval = scheduleInterval;
    }


    public UGWorkerConfig getWorker() {
        return worker;
    }

    public void setWorker(UGWorkerConfig worker) {
        this.worker = worker;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        loadDriverClass();
    }

    private void loadDriverClass() {
        if(worker.getType() == null || UGWorkerTypeEnum.redis == worker.getType()){
            return;
        }

        UGWorkerDataSourceConfig dataSource = worker.getDataSource();
        if(dataSource.getDriverClass() != null && dataSource.getDriverClass().length() > 0){
            return;
        }

        ServiceLoader<Driver> serviceLoader = ServiceLoader.load(Driver.class);
        if (serviceLoader == null) {
            return;
        }

        for (Driver driver : serviceLoader) {
            String driverClassName = driver.getClass().getName();
            if(driverClassName.toLowerCase().contains(worker.getType().name())){
                dataSource.setDriverClass(driverClassName);
                return;
            }
        }
    }
}
