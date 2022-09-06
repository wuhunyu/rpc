package com.wuhunyu.rpc.common.decoder;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * 自定义 基于字段长度 的解码器
 *
 * @author wuhunyu
 * @version 1.0
 * @date 2022-09-03 20:45
 */

public final class SelfLengthFieldBasedFrameDecoder extends LengthFieldBasedFrameDecoder {

    public SelfLengthFieldBasedFrameDecoder(int maxFrameLength) {
        super(maxFrameLength, 21, 4, 7, 0);
    }

}
