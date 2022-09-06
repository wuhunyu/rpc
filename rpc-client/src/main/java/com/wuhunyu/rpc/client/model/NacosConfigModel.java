package com.wuhunyu.rpc.client.model;

import com.wuhunyu.rpc.common.config.ConfigProperties;
import com.wuhunyu.rpc.common.utils.YamlUtil;
import com.wuhunyu.rpc.client.constants.ClientConstant;
import lombok.Data;

import java.io.IOException;

import static com.wuhunyu.rpc.common.constants.NacosConstant.*;

/**
 * nacos 配置
 *
 * @author wuhunyu
 * @version 1.0
 * @date 2022-09-05 20:48
 */

@Data
public class NacosConfigModel {

    /**
     * ===============================================nacos服务==========================================================
     */

    /**
     * nacos 服务地址
     */
    private String serverAddr;

    /**
     * nacos 命名空间
     */
    private String namespace;

    /**
     * nacos 分组
     */
    private String group;

    /**
     * 初始化 nacos 配置属性
     *
     * @throws IOException IOException
     */
    public static NacosConfigModel initProperties() throws IOException {
        // 读取配置
        NacosConfigModel nacosConfigModel =
                YamlUtil.readConfig(ClientConstant.NACOS_CONFIG_LOCATION, NacosConfigModel.class);
        if (nacosConfigModel == null) {
            return null;
        }
        // 将配置写入全局容器中
        ConfigProperties.putProperty(SERVER_ADDR, nacosConfigModel.getServerAddr());
        ConfigProperties.putProperty(NAMESPACE, nacosConfigModel.getNamespace());
        ConfigProperties.putProperty(GROUP, nacosConfigModel.getGroup());
        return nacosConfigModel;
    }

}
