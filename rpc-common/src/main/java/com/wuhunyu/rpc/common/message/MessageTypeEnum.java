package com.wuhunyu.rpc.common.message;

/**
 * 消息类型
 *
 * @author wuhunyu
 * @version 1.0
 * @date 2022-08-31 17:05
 */

public enum MessageTypeEnum {

    /**
     * 请求消息
     */
    REQUEST(0, RequestMessage.class, "请求消息"),

    /**
     * 响应消息
     */
    RESPONSE(1, ResponseMessage.class, "响应消息"),

    /**
     * ping消息
     */
    PING(2, PingMessage.class, "ping消息"),

    /**
     * pong消息
     */
    PONG(3, PongMessage.class, "pong消息");

    MessageTypeEnum(int type, Class<? extends Message> clazz, String description) {
        this.type = type;
        this.clazz = clazz;
        this.description = description;
    }

    private final int type;

    private final Class<? extends Message> clazz;

    private final String description;

    public int getType() {
        return type;
    }

    public Class<? extends Message> getClazz() {
        return clazz;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 查找消息类型Class
     *
     * @param type 消息类型
     * @return 消息类型Class
     */
    public static Class<? extends Message> findMessageTypeByType(int type) {
        for (MessageTypeEnum messageTypeEnum : MessageTypeEnum.values()) {
            if (messageTypeEnum.type == type) {
                return messageTypeEnum.getClazz();
            }
        }
        return null;
    }

}
