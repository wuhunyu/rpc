package com.wuhunyu.rpc.client.handler;

import com.wuhunyu.rpc.common.message.PingMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * 空闲检测机制
 *
 * @author wuhunyu
 * @version 1.0
 * @date 2022-09-05 11:28
 */

@ChannelHandler.Sharable
@Slf4j
public class IdleHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            // 写事件
            if (idleStateEvent.state() == IdleStateEvent.WRITER_IDLE_STATE_EVENT.state()) {
                ctx.writeAndFlush(new PingMessage());
            }
        }
    }
}
