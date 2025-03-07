package io.github.xiapxx.uid.generator.impl.utils;

/**
 * @Author xiapeng
 * @Date 2025-03-06 14:26
 */
public class UGAssertUtils {

    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

}
