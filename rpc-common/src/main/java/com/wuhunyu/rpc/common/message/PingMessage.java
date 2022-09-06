package com.wuhunyu.rpc.common.message;

import com.wuhunyu.rpc.common.config.ConfigProperties;
import com.wuhunyu.rpc.common.constants.CommonConstant;
import com.wuhunyu.rpc.common.sequence.SequenceUtil;
import com.wuhunyu.rpc.common.serialize.SerializeTypeEnum;
import io.netty.channel.ChannelHandler;

/**
 * ping 消息
 *
 * @author wuhunyu
 * @version 1.0
 * @date 2022-09-05 14:51
 */

@ChannelHandler.Sharable
public class PingMessage extends Message {

    public PingMessage() {
        // 填充默认参数
        super(SequenceUtil.nextId(),
                MessageTypeEnum.PING.getType(),
                ConfigProperties.getProperty(CommonConstant.SERIALIZE_TYPE, Byte.class) == null ?
                        SerializeTypeEnum.JACKSON.getType()
                        : ConfigProperties.getProperty(CommonConstant.SERIALIZE_TYPE, Byte.class),
                ConfigProperties.getProperty(CommonConstant.VERSION, Byte.class) == null ?
                        CommonConstant.DEFAULT_VERSION
                        : ConfigProperties.getProperty(CommonConstant.VERSION, Byte.class));
    }

}
