package com.wuhunyu.rpc.client.loadbalance;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.wuhunyu.rpc.client.RpcClientApplication;
import com.wuhunyu.rpc.client.constants.ClientConstant;
import com.wuhunyu.rpc.common.config.ConfigProperties;
import com.wuhunyu.rpc.common.utils.NacosUtil;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 客户端负载均衡策略
 *
 * @author wuhunyu
 * @version 1.0
 * @date 2022-09-06 8:50
 */

@Slf4j
public abstract class ClientLoadBalance {

    private static final List<Channel> CHANNELS = new CopyOnWriteArrayList<>();

    private static final List<Instance> INSTANCES = new CopyOnWriteArrayList<>();

    private static final Lock LOCK = new ReentrantLock();

    static {
        // 初始化负载均衡主程序
        try {
            // 初始化客户端主程序
            RpcClientApplication.init();

            // 获取所有的服务端实例
            String requestServerName = ConfigProperties.getProperty(ClientConstant.REQUEST_SERVER_NAME, String.class);
            String requestGroup = ConfigProperties.getProperty(ClientConstant.REQUEST_GROUP, String.class);
            ClientLoadBalance.refreshInstance(NacosUtil.listServers(requestServerName, requestGroup));
        } catch (NacosException | IOException e) {
            log.error("初始化主程序异常: {}", e.getLocalizedMessage(), e);
        }
    }

    /**
     * 刷新实例配置
     *
     * @param instances 实例集合
     */
    public static void refreshInstance(List<Instance> instances) {
        LOCK.lock();
        try {
            if (CollUtil.isEmpty(instances)) {
                return;
            }
            ClientLoadBalance.closeChannels();
            CHANNELS.clear();
            INSTANCES.clear();

            ClientLoadBalance.openChannel(instances);
            INSTANCES.addAll(instances);
        } finally {
            LOCK.unlock();
        }
    }

    /**
     * 关闭现有channel
     */
    private static void closeChannels() {
        for (Channel channel : CHANNELS) {
            channel.close();
        }
    }

    /**
     * 开启channel
     *
     * @param instances 实例集合
     */
    private static void openChannel(List<Instance> instances) {
        for (Instance instance : instances) {
            try {
                CHANNELS.add(RpcClientApplication.of(instance.getIp(), instance.getPort()));
            } catch (TimeoutException e) {
                log.info("开启channel失败, instance: {}, 异常: {}", instance, e.getLocalizedMessage(), e);
            }
        }
    }

    /**
     * 获取所有服务实例
     *
     * @return 服务实例
     */
    protected List<Instance> listServerInstances() {
        return INSTANCES;
    }

    /**
     * 获取所有的channel实例
     *
     * @return channel实例
     */
    protected List<Channel> listChannels() {
        return CHANNELS;
    }

    /**
     * 返回一个channel实例
     *
     * @return channel实例
     */
    public abstract Channel findChannel();

}
