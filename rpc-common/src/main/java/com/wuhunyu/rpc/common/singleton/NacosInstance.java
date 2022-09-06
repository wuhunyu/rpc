package com.wuhunyu.rpc.common.singleton;

import cn.hutool.core.util.StrUtil;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.wuhunyu.rpc.common.config.ConfigProperties;
import com.wuhunyu.rpc.common.constants.NacosConstant;

import java.util.Properties;

/**
 * Nacos 单例
 *
 * @author wuhunyu
 * @version 1.0
 * @date 2022-09-03 9:07
 */

public enum NacosInstance {

    /**
     * Nacos 实例
     */
    INSTANCE;

    /**
     * 配置中心对象
     */
    private final ConfigService configService;

    /**
     * 注册中心对象
     */
    private final NamingService namingService;

    NacosInstance() {
        // 地址
        String serverAddr = ConfigProperties.getProperty(NacosConstant.SERVER_ADDR, String.class);
        // 命名空间
        String namespace = ConfigProperties.getPropertyOrDefault(NacosConstant.NAMESPACE, String.class, NacosConstant.DEFAULT_NAMESPACE);
        if (StrUtil.isBlank(serverAddr)) {
            throw new IllegalArgumentException("serverAddr不允许为空");
        }

        Properties properties = new Properties();
        properties.put(NacosConstant.SERVER_ADDR, serverAddr);
        properties.put(NacosConstant.NAMESPACE, namespace);

        try {
            configService = NacosFactory.createConfigService(properties);
            namingService = NamingFactory.createNamingService(properties);
        } catch (NacosException e) {
            throw new IllegalStateException("创建配置中心对象 或 注册中心对象异常");
        }
    }

    /**
     * 获取配置中心
     *
     * @return ConfigService
     */
    public ConfigService getConfigService() {
        return configService;
    }

    /**
     * 获取注册中心
     *
     * @return NamingService
     */
    public NamingService getNamingService() {
        return namingService;
    }

}
