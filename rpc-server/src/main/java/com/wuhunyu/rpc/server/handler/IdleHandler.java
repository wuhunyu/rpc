package com.wuhunyu.rpc.server.handler;

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
            // 读事件
            if (idleStateEvent.state() == IdleStateEvent.READER_IDLE_STATE_EVENT.state()) {
                log.info("客户端超时未响应");
                // 强制主动断开连接
                ctx.close();
            }
        }
    }
}
