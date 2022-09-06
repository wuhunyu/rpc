package com.wuhunyu.rpc.server.handler;

import com.wuhunyu.rpc.common.message.RequestMessage;
import com.wuhunyu.rpc.common.message.ResponseMessage;
import com.wuhunyu.rpc.server.singleton.TheadPoolInstance;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * 请求消息处理器
 *
 * @author wuhunyu
 * @version 1.0
 * @date 2022-09-03 21:21
 */

@Slf4j
@ChannelHandler.Sharable
public class RequestMessageHandler extends SimpleChannelInboundHandler<RequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RequestMessage requestMessage) {
        // 获取指定接口的实现
        Class<?> implBean = RpcServerHandler.findBeanByBeanName(requestMessage.getInterfaceName());
        if (implBean == null) {
            log.info("加载接口异常");
            // 返回异常信息
            channelHandlerContext.writeAndFlush(
                    new ResponseMessage(requestMessage, new IllegalArgumentException("加载接口异常")));
            return;
        }
        // 反射
        try {
            Method method = implBean.getMethod(requestMessage.getMethodName(), requestMessage.getParamsTypes());
            // 获取实现类的实例
            Constructor<?> constructor = implBean.getDeclaredConstructor();
            constructor.setAccessible(true);
            Object instance = constructor.newInstance();

            // 异步执行
            CompletableFuture<Object> completableFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    method.setAccessible(true);
                    return method.invoke(instance, requestMessage.getParams());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    log.warn("执行异常: {}", e.getLocalizedMessage(), e);
                    channelHandlerContext.writeAndFlush(
                            new ResponseMessage(requestMessage, new IllegalStateException("执行异常")));
                }
                return null;
            }, TheadPoolInstance.INSTANCE.getThreadPoolExecutor())
                    .exceptionally(throwable -> {
                        log.warn("执行异常: {}", throwable.getLocalizedMessage(), throwable);
                        channelHandlerContext.writeAndFlush(
                                new ResponseMessage(requestMessage, new IllegalStateException("执行异常")));
                        return null;
                    });
            // 阻塞 最多 3秒
            try {
                Object res = completableFuture.get(10, TimeUnit.SECONDS);
                channelHandlerContext.writeAndFlush(new ResponseMessage(requestMessage, res));
            } catch (TimeoutException e) {
                log.warn("执行超时: {}", e.getLocalizedMessage(), e);
                channelHandlerContext.writeAndFlush(
                        new ResponseMessage(requestMessage, new IllegalStateException("执行超时")));
            }
        } catch (NoSuchMethodException e) {
            // 方法不存在
            log.info("调用方法不存在: {}", e.getLocalizedMessage(), e);
            channelHandlerContext.writeAndFlush(
                    new ResponseMessage(requestMessage, new IllegalArgumentException(e.getLocalizedMessage())));
        } catch (Exception e) {
            // 调用失败
            log.warn("调用失败: {}", e.getLocalizedMessage(), e);
            channelHandlerContext.writeAndFlush(
                    new ResponseMessage(requestMessage, new IllegalArgumentException(e.getLocalizedMessage())));
        }
    }
}
