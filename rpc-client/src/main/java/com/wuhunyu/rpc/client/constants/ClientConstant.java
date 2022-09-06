package com.wuhunyu.rpc.client.constants;

/**
 * 客户端 常量
 *
 * @author wuhunyu
 * @version 1.0
 * @date 2022-09-06 8:16
 */

public final class ClientConstant {

    private ClientConstant() {
    }

    /**
     * nacos配置
     */
    public static final String NACOS_CONFIG_LOCATION = "nacos-server-config.yml";

    /**
     * 客户端配置文件地址
     */
    public static final String CONFIG_LOCATION = "rpc-client-config.yml";

    /**
     * 请求服务名称
     */
    public static final String REQUEST_SERVER_NAME = "requestServerName";

    /**
     * 请求服务所在分组
     */
    public static final String REQUEST_GROUP = "requestGroup";

    /**
     * 负载均衡策略
     */
    public static final String LOAD_BALANCE_TYPE = "loadBalanceType";

}
