package com.wuhunyu.rpc.server.singleton;

import cn.hutool.core.thread.NamedThreadFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池单例
 *
 * @author wuhunyu
 * @version 1.0
 * @date 2022-09-05 10:52
 */

public enum TheadPoolInstance {

    /**
     * 线程池实例
     */
    INSTANCE;

    /**
     * 线程前缀
     */
    private static final String THREAD_POOL_PREFIX = "self-thread-pool-";

    private final ThreadPoolExecutor threadPoolExecutor;

    TheadPoolInstance() {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        int maxAvailableProcessors = availableProcessors << 1;
        threadPoolExecutor = new ThreadPoolExecutor(
                availableProcessors,
                maxAvailableProcessors,
                60L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(4),
                new NamedThreadFactory(THREAD_POOL_PREFIX, false),
                new ThreadPoolExecutor.AbortPolicy());
    }

    public ThreadPoolExecutor getThreadPoolExecutor() {
        return threadPoolExecutor;
    }
}
