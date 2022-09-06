package com.wuhunyu.rpc.common.sequence;

import com.wuhunyu.rpc.common.config.ConfigProperties;

import static com.wuhunyu.rpc.common.constants.CommonConstant.*;

/**
 * 序列生成 工具
 *
 * @author wuhunyu
 * @version 1.0
 * @date 2022-09-03 12:12
 */

public enum SequenceUtil {

    /**
     * 序列生成 实例
     */
    INSTANCE;

    private final Sequence sequence;

    SequenceUtil() {
        // 避免多服务冲突，需要设置不同的 机房id 和 机器id, 取值都在[0, 31]
        Integer dataCenterId = ConfigProperties.getProperty(DATA_CENTER_ID, Integer.class);
        Integer workId = ConfigProperties.getProperty(WORKER_ID, Integer.class);
        sequence = (dataCenterId == null || workId == null) ? new Sequence() : new Sequence(dataCenterId, workId);
    }

    /**
     * 获取下一个序列
     *
     * @return 下一个唯一序列
     */
    public static long nextId() {
        return SequenceUtil.INSTANCE.sequence.nextId();
    }

}
