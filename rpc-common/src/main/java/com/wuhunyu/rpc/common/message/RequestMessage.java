package com.wuhunyu.rpc.common.message;

import com.wuhunyu.rpc.common.config.ConfigProperties;
import com.wuhunyu.rpc.common.constants.CommonConstant;
import com.wuhunyu.rpc.common.sequence.SequenceUtil;
import com.wuhunyu.rpc.common.serialize.SerializeTypeEnum;
import lombok.*;

import java.io.Serializable;

/**
 * 请求消息
 *
 * @author wuhunyu
 * @version 1.0
 * @date 2022-09-03 16:25
 */

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class RequestMessage extends Message implements Serializable {

    /**
     * 接口全路径
     */
    private String interfaceName;

    /**
     * 方法名称
     */
    private String methodName;

    /**
     * 参数列表
     */
    private Object[] params;

    /**
     * 参数列表类型
     */
    private Class<?>[] paramsTypes;

    public RequestMessage(String interfaceName, String methodName, Object[] params, Class<?>[] paramsTypes) {
        // 填充默认参数
        super(SequenceUtil.nextId(),
                MessageTypeEnum.RESPONSE.getType(),
                ConfigProperties.getProperty(CommonConstant.SERIALIZE_TYPE, Byte.class) == null ?
                        SerializeTypeEnum.JACKSON.getType()
                        : ConfigProperties.getProperty(CommonConstant.SERIALIZE_TYPE, Byte.class),
                ConfigProperties.getProperty(CommonConstant.VERSION, Byte.class) == null ?
                        CommonConstant.DEFAULT_VERSION
                        : ConfigProperties.getProperty(CommonConstant.VERSION, Byte.class));
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.params = params;
        this.paramsTypes = paramsTypes;
    }

    public RequestMessage(Long sequenceId, Integer messageType, Byte serializeType, Byte version,
                          String interfaceName, String methodName, Object[] params, Class<?>[] paramsTypes) {
        super(sequenceId, messageType, serializeType, version);
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.params = params;
        this.paramsTypes = paramsTypes;
    }
}
