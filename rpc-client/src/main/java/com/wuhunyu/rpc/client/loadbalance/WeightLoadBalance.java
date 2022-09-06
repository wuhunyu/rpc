package com.wuhunyu.rpc.client.loadbalance;

import io.netty.channel.Channel;

/**
 * TODO 权重 负载均衡
 *
 * @author wuhunyu
 * @version 1.0
 * @date 2022-09-06 9:18
 */

class WeightLoadBalance extends ClientLoadBalance {

    @Override
    public Channel findChannel() {
        return null;
    }
}
