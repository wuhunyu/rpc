package com.wuhunyu.rpc.client.model;

import com.wuhunyu.rpc.common.config.ConfigProperties;
import com.wuhunyu.rpc.common.utils.YamlUtil;
import lombok.Data;

import static com.wuhunyu.rpc.common.constants.CommonConstant.*;
import static com.wuhunyu.rpc.common.constants.CommonConstant.VERSION;
import static com.wuhunyu.rpc.client.constants.ClientConstant.*;

/**
 * 客户端 配置信息
 *
 * @author wuhunyu
 * @version 1.0
 * @date 2022-09-06 8:20
 */

@Data
public class RpcClientModel {

    /**
     * ===============================================序列号=============================================================
     */

    /**
     * 数据中心
     */
    private Integer dataCenterId;

    /**
     * 工作中心
     */
    private Integer workId;

    /**
     * ===============================================序列化方式==========================================================
     */

    /**
     * 序列化方式
     */
    private Integer serializeType;

    /**
     * ===============================================请求服务配置========================================================
     */

    /**
     * 请求服务名称
     */
    private String requestServerName;

    /**
     * 请求服务所在分组
     */
    private String requestGroup;

    /**
     * 负载均衡策略
     */
    private Integer loadBalanceType;

    /**
     * ===============================================其他配置===========================================================
     */

    /**
     * 当前版本
     */
    private Byte version;

    /**
     * 初始化客户端配置
     *
     * @param configStr 配置字符串
     * @return 客户端配置信息
     */
    public static RpcClientModel initProperties(String configStr) {
        RpcClientModel rpcClientModel = YamlUtil.readConfigStr(configStr, RpcClientModel.class);
        if (rpcClientModel == null) {
            return null;
        }

        // 将配置写入全局容器中
        ConfigProperties.putProperty(DATA_CENTER_ID, rpcClientModel.getDataCenterId());
        ConfigProperties.putProperty(WORKER_ID, rpcClientModel.getWorkId());

        ConfigProperties.putProperty(SERIALIZE_TYPE, rpcClientModel.getSerializeType());

        ConfigProperties.putProperty(REQUEST_SERVER_NAME, rpcClientModel.getRequestServerName());
        ConfigProperties.putProperty(REQUEST_GROUP, rpcClientModel.getRequestGroup());
        ConfigProperties.putProperty(LOAD_BALANCE_TYPE, rpcClientModel.getLoadBalanceType());

        ConfigProperties.putProperty(VERSION, rpcClientModel.getVersion());

        return rpcClientModel;
    }

}
