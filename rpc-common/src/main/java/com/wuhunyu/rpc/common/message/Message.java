package com.wuhunyu.rpc.common.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * rpc 消息对象
 *
 * @author wuhunyu
 * @version 1.0
 * @date 2022-09-03 10:16
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message implements Serializable {

    /**
     * 序列号
     */
    private Long sequenceId;

    /**
     * 消息类型
     */
    private Integer messageType;

    /**
     * 序列化类型
     */
    private Byte serializeType;

    /**
     * 版本号
     */
    private Byte version;

}
