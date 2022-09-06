package com.wuhunyu.rpc.common.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wuhunyu.rpc.common.constants.CommonConstant;
import com.wuhunyu.rpc.common.constants.NacosConstant;
import com.wuhunyu.rpc.common.singleton.JacksonInstance;
import com.wuhunyu.rpc.common.singleton.NacosInstance;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;

/**
 * Nacos 操作工具类
 *
 * @author wuhunyu
 * @version 1.0
 * @date 2022-09-02 23:02
 */

@Slf4j
public final class NacosUtil {

    private NacosUtil() {
    }

    /**
     * 以json对象配置的形式发布配置信息
     *
     * @param dataId  dataId
     * @param group   可为空，group
     * @param jsonObj 配置对象
     * @return 配置发布是否成功(true : 成功 ; false : 失败)
     * @throws NacosException NacosException
     */
    public static boolean publishConfig(String dataId, String group, Object jsonObj) throws NacosException {
        log.debug("发布配置, dataId: {}, group: {}, jsonObj: {}", dataId, group, jsonObj);
        if (jsonObj == null) {
            throw new IllegalArgumentException("配置对象不能为空");
        }

        // 序列化
        ObjectMapper objectMapper = JacksonInstance.INSTANCE.getObjectMapper();
        String jsonStr = null;
        try {
            jsonStr = objectMapper.writeValueAsString(jsonObj);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("jsonObj: " + jsonObj + ", 序列化失败");
        }

        // 配置中心对象
        ConfigService configService = NacosInstance.INSTANCE.getConfigService();
        return configService.publishConfig(dataId,
                StrUtil.isBlank(group) ? NacosConstant.DEFAULT_GROUP : group,
                jsonStr, ConfigType.JSON.getType());
    }

    /**
     * 获取指定 dataId 的配置
     *
     * @param dataId dataId
     * @param group  可为空，group
     * @return 配置字符串
     * @throws NacosException NacosException
     */
    public static String findConfig(String dataId, String group) throws NacosException {
        log.debug("获取配置, dataId: {}, group: {}", dataId, group);
        if (StrUtil.isBlank(dataId)) {
            return CommonConstant.BLANK_STR;
        }
        group = StrUtil.isBlank(group) ? NacosConstant.DEFAULT_GROUP : group;
        // 配置中心对象
        ConfigService configService = NacosInstance.INSTANCE.getConfigService();
        return configService.getConfig(dataId, group, 200L);
    }

    /**
     * 移除指定配置
     *
     * @param dataId dataId
     * @param group  可为空，group
     * @return 移除配置是否成功(true : 成功 ; false : 失败)
     * @throws NacosException NacosException
     */
    public static boolean removeConfig(String dataId, String group) throws NacosException {
        log.debug("移除配置, dataId: {}, group: {}", dataId, group);
        if (StrUtil.isBlank(dataId)) {
            return false;
        }
        // 配置中心对象
        ConfigService configService = NacosInstance.INSTANCE.getConfigService();
        return configService.removeConfig(dataId,
                StrUtil.isBlank(group) ? NacosConstant.DEFAULT_GROUP : group);
    }

    /**
     * 注册一个服务
     *
     * @param serverName 服务名称
     * @param group      可为空，分组
     * @param ip         服务访问ip
     * @param port       服务暴露端口
     * @param weight     可为空，访问权重，默认为1
     * @throws NacosException NacosException
     */
    public static void registerServer(String serverName, String group, String ip, Integer port, Double weight)
            throws NacosException {
        log.debug("注册服务, serverName: {}, group: {}, ip: {}, port: {}, weight: {}",
                serverName, group, ip, port, weight);

        // 参数非空校验
        if (StrUtil.isBlank(serverName) || StrUtil.isBlank(ip) || port == null) {
            throw new IllegalArgumentException("serverName: " + serverName + " ,ip: " + ip + " ,port: " + port + "不能为空");
        }
        Instance instance = new Instance();
        instance.setIp(ip);
        instance.setPort(port);
        // 权重
        instance.setWeight(weight == null || weight < 1 ? 1D : weight);
        // 集群名称
        instance.setClusterName(serverName);

        // 获取注册中心
        NamingService namingService = NacosInstance.INSTANCE.getNamingService();
        namingService.registerInstance(serverName,
                StrUtil.isBlank(group) ? NacosConstant.DEFAULT_GROUP : group,
                instance);
    }

    /**
     * 获取指定服务名称和分组的服务实例
     *
     * @param serverName 服务名称
     * @param group      可为空，分组
     * @return 服务实例
     * @throws NacosException NacosException
     */
    @SuppressWarnings("unchecked")
    public static List<Instance> listServers(String serverName, String group) throws NacosException {
        log.debug("获取服务, serverName: {}, group: {}", serverName, group);

        // 参数非空校验
        if (StrUtil.isBlank(serverName)) {
            throw new IllegalArgumentException("serverName: " + serverName + "不能为空");
        }
        group = StrUtil.isBlank(group) ? NacosConstant.DEFAULT_GROUP : group;

        // 获取注册中心
        NamingService namingService = NacosInstance.INSTANCE.getNamingService();
        List<Instance> instances = namingService.selectInstances(serverName, group, true);
        return CollUtil.isEmpty(instances) ? Collections.EMPTY_LIST : instances;
    }

    /**
     * 主动移除一个服务
     *
     * @param serverName 服务名称
     * @param group      可为空，分组
     * @param ip         服务访问ip
     * @param port       服务暴露端口
     */
    public static void deRegisterServer(String serverName, String group, String ip, Integer port) throws NacosException {
        log.debug("移除服务, serverName: {}, group: {}, ip: {}, port: {}", serverName, group, ip, port);
        if (StrUtil.isBlank(serverName) || StrUtil.isBlank(ip) || port == null) {
            return;
        }
        // 获取注册中心
        NamingService namingService = NacosInstance.INSTANCE.getNamingService();
        namingService.deregisterInstance(serverName,
                StrUtil.isBlank(group) ? NacosConstant.DEFAULT_GROUP : group,
                ip, port);
    }

}
