package com.wuhunyu.rpc.common.singleton;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Jackson单例
 *
 * @author wuhunyu
 * @version 1.0
 * @date 2022-09-02 23:04
 */

public enum JacksonInstance {

    /**
     * jackson 单例
     */
    INSTANCE;

    private final ObjectMapper objectMapper;

    JacksonInstance() {
        objectMapper = new ObjectMapper();
        // 注册对 LocalDatetime 序列化的支持
        objectMapper.findAndRegisterModules();
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

}
