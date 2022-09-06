package com.wuhunyu.rpc.common.constants;

import com.alibaba.nacos.api.PropertyKeyConst;

/**
 * Nacos常量
 *
 * @author wuhunyu
 * @version 1.0
 * @date 2022-09-03 9:09
 */

public final class NacosConstant {

    private NacosConstant() {
    }

    /**
     * 配置中心/注册中心 地址key
     */
    public static final String SERVER_ADDR = PropertyKeyConst.SERVER_ADDR;

    /**
     * 命名空间key
     */
    public static final String NAMESPACE = PropertyKeyConst.NAMESPACE;

    /**
     * 分组
     */
    public static final String GROUP = "group";

    /**
     * 默认命名空间
     */
    public static final String DEFAULT_NAMESPACE = "";

    /**
     * 默认分组
     */
    public static final String DEFAULT_GROUP = "DEFAULT_GROUP";

}
