package com.wuhunyu.rpc.common.message;

import io.netty.channel.ChannelHandler;

/**
 * pong 消息
 *
 * @author wuhunyu
 * @version 1.0
 * @date 2022-09-05 14:51
 */

@ChannelHandler.Sharable
public class PongMessage extends Message {

    public PongMessage(Long sequenceId, Byte serializeType, Byte version) {
        super(sequenceId, MessageTypeEnum.PONG.getType(), serializeType, version);
    }

}
