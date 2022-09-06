package com.wuhunyu.rpc.client.handler;

import com.wuhunyu.rpc.common.message.ResponseMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * 连接 & 异常处理
 *
 * @author wuhunyu
 * @version 1.0
 * @date 2022-09-05 13:46
 */

@Slf4j
@ChannelHandler.Sharable
public class ConnectionAndExceptionHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("连接建立: {}", ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("连接断开: {}", ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 兜底异常
        log.warn("兜底异常：{}", cause.getLocalizedMessage(), cause);
        // 返回默认异常
        ctx.writeAndFlush(new ResponseMessage(new IllegalStateException("系统资源繁忙")));
    }
}
