package com.wuhunyu.rpc.server.constants;

/**
 * 服务端常量
 *
 * @author wuhunyu
 * @version 1.0
 * @date 2022-09-03 21:40
 */

public final class ServerConstant {

    private ServerConstant() {
    }

    /**
     * 服务名称
     */
    public static final String SERVER_NAME = "rpc-server";

    /**
     * nacos配置
     */
    public static final String NACOS_CONFIG_LOCATION = "nacos-server-config.yml";

    /**
     * 服务端配置文件地址
     */
    public static final String CONFIG_LOCATION = "rpc-server-config.yml";

    /**
     * 服务暴露扫描包路径
     */
    public static final String RPC_SERVER_SCAN = "rpcServerScan";

    /**
     * 暴露服务名称
     */
    public static final String EXPOSE_SERVER_NAME = "exposeServerName";

    /**
     * 暴露服务所在分组
     */
    public static final String EXPOSE_GROUP = "exposeGroup";

    /**
     * 暴露服务ip
     */
    public static final String EXPOSE_IP = "exposeIp";

    /**
     * 暴露服务端口
     */
    public static final String EXPOSE_PORT = "exposePort";

    /**
     * 暴露服务权重
     */
    public static final String EXPOSE_WEIGHT = "exposeWeight";

}
