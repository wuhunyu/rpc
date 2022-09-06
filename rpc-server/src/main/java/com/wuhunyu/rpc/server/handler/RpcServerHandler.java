package com.wuhunyu.rpc.server.handler;

import cn.hutool.core.util.StrUtil;
import com.wuhunyu.rpc.server.utils.BeanScanUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * rpc服务处理
 *
 * @author wuhunyu
 * @version 1.0
 * @date 2022-09-03 21:39
 */

public class RpcServerHandler {

    private static final Map<String, Class<?>> SERVER_MAP = new ConcurrentHashMap<>();

    /**
     * 扫描指定包下的全部被 @RpcServer 注解修饰的类
     *
     * @param scanPackage 需要扫描的包
     */
    public static void scanAllServer(String scanPackage) {
        SERVER_MAP.putAll(BeanScanUtil.collectBeans(scanPackage));
    }

    /**
     * 根据bean名称获取bean
     *
     * @param beanName bean名称
     * @return bean
     */
    public static Class<?> findBeanByBeanName(String beanName) {
        if (StrUtil.isBlank(beanName)) {
            return null;
        }
        return SERVER_MAP.get(beanName);
    }

}
