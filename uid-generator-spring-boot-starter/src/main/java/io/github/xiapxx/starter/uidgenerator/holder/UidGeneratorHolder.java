package io.github.xiapxx.starter.uidgenerator.holder;

import io.github.xiapxx.uid.generator.api.UidGenerator;
import io.github.xiapxx.uid.generator.impl.utils.UGAssertUtils;

/**
 * @Author xiapeng
 * @Date 2025-03-26 17:57
 */
public class UidGeneratorHolder {

    private static UidGenerator UID_GENERATOR = null;

    public UidGeneratorHolder(UidGenerator uidGenerator) {
        UID_GENERATOR = uidGenerator;
    }

    /**
     * 获取下一个id
     *
     * @return 下一个id
     */
    public static long nextUID() {
        UGAssertUtils.isTrue(UID_GENERATOR != null, "UidGenerator未初始化");
        return UID_GENERATOR.getUID();
    }
}
