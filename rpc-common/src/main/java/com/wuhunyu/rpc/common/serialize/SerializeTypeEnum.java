package com.wuhunyu.rpc.common.serialize;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wuhunyu.rpc.common.message.Message;

import java.io.*;

/**
 * 序列化类型
 *
 * @author wuhunyu
 * @version 1.0
 * @date 2022-08-31 17:09
 */

public enum SerializeTypeEnum implements Serialize {

    /**
     * jdk序列化
     */
    JDK(0, "jdk序列化") {
        @Override
        public byte[] serialize(Message message) throws IOException {
            try (
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(byteArrayOutputStream)
            ) {
                oos.writeObject(message);
                return byteArrayOutputStream.toByteArray();
            }
        }

        @Override
        public Message deSerialize(byte[] bytes, Class<? extends Message> clazz) throws IOException, ClassNotFoundException {
            try (
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
                    ObjectInputStream oos = new ObjectInputStream(byteArrayInputStream)
            ) {
                return clazz.cast(oos.readObject());
            }
        }
    },

    /**
     * jackson序列化
     */
    JACKSON(1, "jackson序列化") {
        @Override
        public byte[] serialize(Message message) throws JsonProcessingException {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsBytes(message);
        }

        @Override
        public Message deSerialize(byte[] bytes, Class<? extends Message> clazz) throws IOException {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(bytes, clazz);
        }
    },
    ;

    SerializeTypeEnum(int type, String description) {
        this.type = (byte) type;
        this.description = description;
    }

    private final byte type;

    private final String description;

    public byte getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 查找指定的序列化方式
     *
     * @param type 序列化方式
     * @return 序列化实现
     */
    public static SerializeTypeEnum findSerializeTypeByType(byte type) {
        for (SerializeTypeEnum serializeTypeEnum : SerializeTypeEnum.values()) {
            if (serializeTypeEnum.type == type) {
                return serializeTypeEnum;
            }
        }
        return null;
    }

}
