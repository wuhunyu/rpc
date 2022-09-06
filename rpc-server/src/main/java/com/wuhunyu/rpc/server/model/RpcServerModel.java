package com.wuhunyu.rpc.server.model;

import com.wuhunyu.rpc.common.config.ConfigProperties;
import com.wuhunyu.rpc.common.utils.YamlUtil;
import com.wuhunyu.rpc.server.constants.ServerConstant;
import lombok.Data;

import static com.wuhunyu.rpc.common.constants.CommonConstant.*;
import static com.wuhunyu.rpc.server.constants.ServerConstant.*;

/**
 * 服务端 模型
 *
 * @author wuhunyu
 * @version 1.0
 * @date 2022-09-05 20:40
 */

@Data
public class RpcServerModel {

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
     * ===============================================暴露服务配置========================================================
     */

    /**
     * 暴露服务名称
     */
    private String exposeServerName;

    /**
     * 暴露服务所在分组
     */
    private String exposeGroup;

    /**
     * 暴露服务ip
     */
    private String exposeIp;

    /**
     * 暴露服务端口
     */
    private Integer exposePort;

    /**
     * 暴露服务权重
     */
    private Double exposeWeight;

    /**
     * ===============================================其他配置===========================================================
     */

    /**
     * 当前版本
     */
    private Byte version;

    /**
     * 扫描包路径
     */
    private String scanPackage;

    /**
     * 初始化服务端配置
     *
     * @param configStr 配置字符串
     * @return 服务端配置信息
     */
    public static RpcServerModel initProperties(String configStr) {
        RpcServerModel rpcServerModel = YamlUtil.readConfigStr(configStr, RpcServerModel.class);
        if (rpcServerModel == null) {
            return null;
        }
        // 将配置写入全局容器中
        ConfigProperties.putProperty(DATA_CENTER_ID, rpcServerModel.getDataCenterId());
        ConfigProperties.putProperty(WORKER_ID, rpcServerModel.getWorkId());

        ConfigProperties.putProperty(SERIALIZE_TYPE, rpcServerModel.getSerializeType());

        ConfigProperties.putProperty(EXPOSE_SERVER_NAME, rpcServerModel.getExposeServerName());
        ConfigProperties.putProperty(EXPOSE_GROUP, rpcServerModel.getExposeGroup());
        ConfigProperties.putProperty(EXPOSE_IP, rpcServerModel.getExposeIp());
        ConfigProperties.putProperty(EXPOSE_PORT, rpcServerModel.getExposePort());
        ConfigProperties.putProperty(EXPOSE_WEIGHT, rpcServerModel.getExposeWeight());

        ConfigProperties.putProperty(VERSION, rpcServerModel.getVersion());
        ConfigProperties.putProperty(ServerConstant.RPC_SERVER_SCAN, rpcServerModel.getScanPackage());

        return rpcServerModel;
    }

}
