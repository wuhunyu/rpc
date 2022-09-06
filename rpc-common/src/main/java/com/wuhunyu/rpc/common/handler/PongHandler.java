package com.wuhunyu.rpc.common.handler;

import com.wuhunyu.rpc.common.message.PongMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * ping消息处理
 *
 * @author wuhunyu
 * @version 1.0
 * @date 2022-09-05 14:54
 */

@Slf4j
@ChannelHandler.Sharable
public class PongHandler extends SimpleChannelInboundHandler<PongMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, PongMessage pingMessage) throws Exception {
        // 不做任何操作
    }
}
