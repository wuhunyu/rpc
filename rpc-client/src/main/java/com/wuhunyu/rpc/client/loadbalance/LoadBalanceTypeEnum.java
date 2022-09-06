package com.wuhunyu.rpc.client.loadbalance;

/**
 * 负载均衡 类型
 *
 * @author wuhunyu
 * @version 1.0
 * @date 2022-09-06 9:12
 */

public enum LoadBalanceTypeEnum {

    /**
     * 轮询策略
     */
    POLLING(0, PollingLoadBalance.class, "轮询策略"),

    /**
     * 随机策略
     */
    RANDOM(1, RandomLoadBalance.class, "随机策略"),

    /**
     * 权重策略
     */
    weight(2, WeightLoadBalance.class, "权重策略");

    private final int type;

    private final Class<? extends ClientLoadBalance> clazz;

    private final String msg;

    LoadBalanceTypeEnum(int type, Class<? extends ClientLoadBalance> clazz, String msg) {
        this.type = type;
        this.clazz = clazz;
        this.msg = msg;
    }

    public int getType() {
        return type;
    }

    public Class<? extends ClientLoadBalance> getClazz() {
        return clazz;
    }

    public String getMsg() {
        return msg;
    }

    /**
     * 根据type值获取枚举对象
     *
     * @param type type值
     * @return 枚举对象
     */
    public static LoadBalanceTypeEnum findLoadBalanceTypeEnumByType(int type) {
        for (LoadBalanceTypeEnum loadBalanceTypeEnum : LoadBalanceTypeEnum.values()) {
            if (loadBalanceTypeEnum.type == type) {
                return loadBalanceTypeEnum;
            }
        }
        return null;
    }

}
