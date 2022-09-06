package com.wuhunyu.rpc.client.loadbalance;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.nacos.api.naming.pojo.Instance;
import io.netty.channel.Channel;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询策略
 *
 * @author wuhunyu
 * @version 1.0
 * @date 2022-09-06 9:10
 */

class PollingLoadBalance extends ClientLoadBalance {

    /**
     * 计数器
     */
    private final AtomicInteger count = new AtomicInteger(0);

    @Override
    public Channel findChannel() {
        List<Instance> instances = this.listServerInstances();
        if (CollUtil.isEmpty(instances)) {
            return null;
        }
        int index = count.getAndIncrement() % instances.size();
        return this.listChannels().get(index);
    }

}
