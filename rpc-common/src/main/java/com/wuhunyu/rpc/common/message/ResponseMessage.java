package com.wuhunyu.rpc.common.message;

import com.wuhunyu.rpc.common.config.ConfigProperties;
import com.wuhunyu.rpc.common.constants.CommonConstant;
import com.wuhunyu.rpc.common.sequence.SequenceUtil;
import com.wuhunyu.rpc.common.serialize.SerializeTypeEnum;
import lombok.*;

import java.io.Serializable;

/**
 * 响应消息
 *
 * @author wuhunyu
 * @version 1.0
 * @date 2022-09-03 16:38
 */

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ResponseMessage extends Message implements Serializable {

    /**
     * 响应结果
     */
    private Object result;

    /**
     * 异常信息
     */
    private Throwable exception;

    public ResponseMessage(Throwable exception) {
        // 填充默认参数
        super(SequenceUtil.nextId(),
                MessageTypeEnum.RESPONSE.getType(),
                ConfigProperties.getProperty(CommonConstant.SERIALIZE_TYPE, Byte.class) == null ?
                        SerializeTypeEnum.JACKSON.getType()
                        : ConfigProperties.getProperty(CommonConstant.SERIALIZE_TYPE, Byte.class),
                ConfigProperties.getProperty(CommonConstant.VERSION, Byte.class) == null ?
                        CommonConstant.DEFAULT_VERSION
                        : ConfigProperties.getProperty(CommonConstant.VERSION, Byte.class));
        this.exception = exception;
    }

    public ResponseMessage(Long sequenceId, Byte serializeType, Byte version,
                           Object result, Throwable exception) {
        super(sequenceId, MessageTypeEnum.RESPONSE.getType(), serializeType, version);
        this.result = result;
        this.exception = exception;
    }

    public ResponseMessage(Long sequenceId, Byte serializeType, Byte version,
                           Object result) {
        super(sequenceId, MessageTypeEnum.RESPONSE.getType(), serializeType, version);
        this.result = result;
    }

    public ResponseMessage(Long sequenceId, Byte serializeType, Byte version,
                           Throwable exception) {
        super(sequenceId, MessageTypeEnum.RESPONSE.getType(), serializeType, version);
        this.exception = exception;
    }

    public ResponseMessage(RequestMessage requestMessage, Object result) {
        super(requestMessage.getSequenceId(), MessageTypeEnum.RESPONSE.getType(),
                requestMessage.getSerializeType(), requestMessage.getVersion());
        this.result = result;
    }

    public ResponseMessage(RequestMessage requestMessage, Throwable exception) {
        super(requestMessage.getSequenceId(), MessageTypeEnum.RESPONSE.getType(),
                requestMessage.getSerializeType(), requestMessage.getVersion());
        this.exception = exception;
    }

}
