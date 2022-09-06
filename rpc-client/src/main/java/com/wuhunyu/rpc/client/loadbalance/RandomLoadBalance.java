package com.wuhunyu.rpc.client.loadbalance;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.nacos.api.naming.pojo.Instance;
import io.netty.channel.Channel;

import java.security.SecureRandom;
import java.util.List;

/**
 * 随机负载均衡
 *
 * @author wuhunyu
 * @version 1.0
 * @date 2022-09-06 9:17
 */

class RandomLoadBalance extends ClientLoadBalance {

    /**
     * 随机对象
     */
    private final SecureRandom random = new SecureRandom();

    @Override
    public Channel findChannel() {
        List<Instance> instances = this.listServerInstances();
        if (CollUtil.isEmpty(instances)) {
            return null;
        }
        random.setSeed(System.currentTimeMillis());
        int index = random.nextInt() % instances.size();
        return this.listChannels().get(index);
    }

}
