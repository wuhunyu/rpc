package com.wuhunyu.rpc.client.handler;

import com.wuhunyu.rpc.client.promise.PromiseResult;
import com.wuhunyu.rpc.common.config.ConfigProperties;
import com.wuhunyu.rpc.common.constants.CommonConstant;
import com.wuhunyu.rpc.common.message.Message;
import com.wuhunyu.rpc.common.message.MessageTypeEnum;
import com.wuhunyu.rpc.common.message.RequestMessage;
import com.wuhunyu.rpc.common.sequence.SequenceUtil;
import com.wuhunyu.rpc.common.serialize.SerializeTypeEnum;
import io.netty.channel.Channel;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * 动态代理 处理
 *
 * @author wuhunyu
 * @version 1.0
 * @date 2022-09-05 15:47
 */

@Slf4j
public class ProxyInvocationHandler {

    public static final ProxyInvocationHandler PROXY_INVOCATION_HANDLER = new ProxyInvocationHandler();

    public Object invoke(Object proxy, Class<?> interfaceClass, Method method, Object[] args, Channel channel) {
        // 构造请求对象
        long sequenceId = SequenceUtil.nextId();
        RequestMessage requestMessage = new RequestMessage(
                sequenceId,
                MessageTypeEnum.REQUEST.getType(),
                ConfigProperties.getPropertyOrDefault(CommonConstant.SERIALIZE_TYPE, Byte.class,
                        SerializeTypeEnum.JACKSON.getType()),
                ConfigProperties.getPropertyOrDefault(CommonConstant.VERSION, Byte.class,
                        CommonConstant.DEFAULT_VERSION),

                interfaceClass.getName(),
                method.getName(),
                args,
                method.getParameterTypes());
        // 设置结果存放容器
        Promise<Object> promise = PromiseResult.set(sequenceId);
        // 写出消息
        channel.writeAndFlush(requestMessage);
        try {
            // 等待结果响应
            promise.await();
            // 异常
            if (!promise.isSuccess()) {
                Throwable cause = promise.cause();
                throw new IllegalStateException(cause.getMessage());
            }
            return promise.getNow();
        } catch (InterruptedException e) {
            log.error("获取响应结果异常: {}", e.getLocalizedMessage(), e);
        } finally {
            // 移除结果存放容器
            PromiseResult.remove(sequenceId);
        }
        return null;
    }

}
