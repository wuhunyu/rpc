package com.wuhunyu.rpc.client.loadbalance;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * 负载均衡 工厂
 *
 * @author wuhunyu
 * @version 1.0
 * @date 2022-09-06 9:14
 */

@Slf4j
public final class LoadBalanceFactory {

    private LoadBalanceFactory() {
    }

    public static ClientLoadBalance of(int loadBalanceType) {
        // 获取负载均衡枚举类型
        LoadBalanceTypeEnum balanceTypeEnum = LoadBalanceTypeEnum.findLoadBalanceTypeEnumByType(loadBalanceType);
        return LoadBalanceFactory.of(balanceTypeEnum);
    }

    /**
     * 获取负载均衡实例
     *
     * @param balanceTypeEnum 负载均衡类型
     * @return 负载均衡实例
     */
    public static ClientLoadBalance of(LoadBalanceTypeEnum balanceTypeEnum) {
        if (balanceTypeEnum == null) {
            return null;
        }
        Class<? extends ClientLoadBalance> clazz = balanceTypeEnum.getClazz();

        // 获取无参构造
        Constructor<? extends ClientLoadBalance> constructor = null;
        try {
            constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            // 获取实例
            return constructor.newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            log.warn("获取负载均衡实例异常: {}", e.getLocalizedMessage(), e);
        }
        return null;
    }

}
