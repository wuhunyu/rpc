package com.wuhunyu.rpc.common.config;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 全局配置属性
 *
 * @author wuhunyu
 * @version 1.0
 * @date 2022-09-03 16:00
 */

@Slf4j
public final class ConfigProperties {

    private static final Map<String, Object> CONFIG_MAP = new HashMap<>();

    private static final Lock LOCK = new ReentrantLock();

    private ConfigProperties() {
    }

    /**
     * 设置属性
     *
     * @param key key
     * @param val val
     */
    public static void putProperty(String key, Object val) {
        if (key == null || val == null) {
            throw new IllegalArgumentException("key: " + key + ", 或val: " + val + "不能为空");
        }
        LOCK.lock();
        try {
            CONFIG_MAP.put(key, val);
        } finally {
            LOCK.unlock();
        }
    }

    /**
     * 取出属性
     *
     * @param key   key
     * @param clazz 值类型
     * @param <T>   值泛型
     * @return 值
     */
    public static <T> T getProperty(String key, Class<T> clazz) {
        if (key == null || clazz == null) {
            return null;
        }
        LOCK.lock();
        try {
            Object val = CONFIG_MAP.get(key);
            if (val == null) {
                return null;
            }
            if (clazz.isAssignableFrom(String.class)) {
                return clazz.cast(val);
            }
            String str = val.toString();
            Method valueOf = clazz.getDeclaredMethod("valueOf", String.class);
            valueOf.setAccessible(true);
            return clazz.cast(valueOf.invoke(null, str));
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            log.warn("类型转换异常: {}", e.getLocalizedMessage(), e);
        } finally {
            LOCK.unlock();
        }
        return null;
    }

    /**
     * 取出属性(带默认值)
     *
     * @param key        key
     * @param clazz      值类型
     * @param defaultVal 值为空时的默认值
     * @param <T>        值泛型
     * @return 值
     */
    public static <T> T getPropertyOrDefault(String key, Class<T> clazz, T defaultVal) {
        LOCK.lock();
        try {
            T val = ConfigProperties.getProperty(key, clazz);
            return val == null ? defaultVal : val;
        } finally {
            LOCK.unlock();
        }
    }

    public static Map<String, Object> getConfigMap() {
        return CONFIG_MAP;
    }
}
