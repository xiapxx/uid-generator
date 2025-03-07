package io.github.xiapxx.starter.uidgenerator.properties;

import io.github.xiapxx.starter.uidgenerator.enums.UGWorkerTypeEnum;

/**
 * @Author xiapeng
 * @Date 2025-03-07 14:07
 */
public class UGWorkerConfig {

    public static final String PREFIX = UidGeneratorProperties.PREFIX + ".worker";

    /**
     * id的生成方式
     * 支持三种: redis, mysql, postgres
     */
    private UGWorkerTypeEnum type;

    /**
     * 机器id;
     * 如果不配置type
     * 1. 并且配置了id, 使用配置的id
     * 2. 并且没有配置id, 随机生成id
     */
    private Long id;

    /**
     * 如果type=mysql或postgres, 那么此项必须配置
     */
    private UGWorkerDataSourceConfig dataSource = new UGWorkerDataSourceConfig();

    public UGWorkerTypeEnum getType() {
        return type;
    }

    public void setType(UGWorkerTypeEnum type) {
        this.type = type;
    }

    public UGWorkerDataSourceConfig getDataSource() {
        return dataSource;
    }

    public void setDataSource(UGWorkerDataSourceConfig dataSource) {
        this.dataSource = dataSource;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
