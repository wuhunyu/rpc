package com.wuhunyu.rpc.common.serialize;

import com.wuhunyu.rpc.common.message.Message;

/**
 * 序列化接口定义
 *
 * @author wuhunyu
 * @version 1.0
 * @date 2022-08-31 17:13
 */

public interface Serialize {

    /**
     * 序列化
     *
     * @param message 自定义消息类型
     * @return 字节数组
     * @throws Exception 序列化异常
     */
    byte[] serialize(Message message) throws Exception;

    /**
     * 反序列化
     *
     * @param bytes 字节数组
     * @param clazz 反序列化类型
     * @return 自定义消息对象
     * @throws Exception 反序列化异常
     */
    Message deSerialize(byte[] bytes, Class<? extends Message> clazz) throws Exception;

}
