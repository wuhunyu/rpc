package com.wuhunyu.rpc.client.promise;

import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.Promise;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 请求消息管理
 *
 * @author wuhunyu
 * @version 1.0
 * @date 2022-09-05 15:37
 */

public class PromiseResult {

    /**
     * {sequenceId: Promise}
     */
    private static final Map<Long, Promise<Object>> PROMISE_MAP = new ConcurrentHashMap<>();

    /**
     * 线程池
     */
    private static final EventExecutorGroup EVENT_EXECUTOR_GROUP = new DefaultEventExecutorGroup(8);

    /**
     * set
     *
     * @param sequenceId 序列id
     * @return Promise
     */
    public static Promise<Object> set(Long sequenceId) {
        Promise<Object> promise = new DefaultPromise<>(EVENT_EXECUTOR_GROUP.next());
        PROMISE_MAP.put(sequenceId, promise);
        return promise;
    }

    /**
     * get
     *
     * @param sequenceId 序列id
     * @return Promise
     */
    public static Promise<Object> get(Long sequenceId) {
        return PROMISE_MAP.remove(sequenceId);
    }

    /**
     * 移除
     *
     * @param sequenceId 序列id
     */
    public static void remove(Long sequenceId) {
        PROMISE_MAP.remove(sequenceId);
    }

}
