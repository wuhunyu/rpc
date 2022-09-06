package com.wuhunyu.rpc.client.utils;

import com.wuhunyu.rpc.client.constants.ClientConstant;
import com.wuhunyu.rpc.client.handler.ProxyInvocationHandler;
import com.wuhunyu.rpc.client.loadbalance.ClientLoadBalance;
import com.wuhunyu.rpc.client.loadbalance.LoadBalanceFactory;
import com.wuhunyu.rpc.common.config.ConfigProperties;
import io.netty.channel.Channel;

import java.lang.reflect.Proxy;

/**
 * 方法调用 工具类
 *
 * @author wuhunyu
 * @version 1.0
 * @date 2022-09-05 15:32
 */

public final class InvokeUtil {

    private static final ClientLoadBalance CLIENT_LOAD_BALANCE;

    static {
        // 获取负载均衡策略
        Integer loadBalanceType =
                ConfigProperties.getPropertyOrDefault(ClientConstant.LOAD_BALANCE_TYPE, Integer.class, 0);
        CLIENT_LOAD_BALANCE = LoadBalanceFactory.of(loadBalanceType);
    }

    private InvokeUtil() {
    }

    /**
     * 查找一个代理对象
     *
     * @param clazz 类对象
     * @param <T>   类泛型
     * @return 代理对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T findProxyObj(Class<T> clazz) {
        // 获取channel
        Channel channel = CLIENT_LOAD_BALANCE.findChannel();
        if (clazz == null || channel == null) {
            throw new NullPointerException("服务端连接异常");
        }
        // 代理对象
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),
                new Class[]{clazz},
                (proxy, method, args) ->
                        ProxyInvocationHandler.PROXY_INVOCATION_HANDLER.invoke(proxy, clazz, method, args, channel));
    }

}
