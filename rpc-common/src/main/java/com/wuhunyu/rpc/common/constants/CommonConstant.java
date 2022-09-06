package com.wuhunyu.rpc.common.constants;

/**
 * 公共常量
 *
 * @author wuhunyu
 * @version 1.0
 * @date 2022-09-03 19:10
 */

public final class CommonConstant {

    private CommonConstant() {
    }

    /**
     * 空字符串
     */
    public static final String BLANK_STR = "";

    /**
     * 序列化方式
     */
    public static final String SERIALIZE_TYPE = "serializeType";

    /**
     * 版本
     */
    public static final String VERSION = "version";

    /**
     * 默认版本号
     */
    public static final byte DEFAULT_VERSION = 1;

    /**
     * 数据中心
     */
    public static final String DATA_CENTER_ID = "datacenterId";

    /**
     * 工作中心
     */
    public static final String WORKER_ID = "workerId";

}
