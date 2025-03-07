package io.github.xiapxx.uid.generator.impl.utils;

/**
 * @Author xiapeng
 * @Date 2025-03-06 14:48
 */
public class StringUtils {

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static boolean isNotEmpty(String str) {
        return !StringUtils.isEmpty(str);
    }

}
