package com.wuhunyu.rpc.client.handler;

import com.wuhunyu.rpc.client.promise.PromiseResult;
import com.wuhunyu.rpc.common.message.ResponseMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

/**
 * 响应消息 处理
 *
 * @author wuhunyu
 * @version 1.0
 * @date 2022-09-05 15:30
 */

@Slf4j
@ChannelHandler.Sharable
public class ResponseMessageHandler extends SimpleChannelInboundHandler<ResponseMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ResponseMessage responseMessage) {
        // 获取结果存放容器
        Promise<Object> promise = PromiseResult.get(responseMessage.getSequenceId());
        Throwable exception = responseMessage.getException();
        // 异常
        if (exception != null) {
            promise.setFailure(exception);
            return;
        }
        // 返回结果
        promise.setSuccess(responseMessage.getResult());
    }
}
