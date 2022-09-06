package com.wuhunyu.rpc.common.codec;

import com.wuhunyu.rpc.common.config.ConfigProperties;
import com.wuhunyu.rpc.common.constants.CommonConstant;
import com.wuhunyu.rpc.common.message.Message;
import com.wuhunyu.rpc.common.message.MessageTypeEnum;
import com.wuhunyu.rpc.common.serialize.SerializeTypeEnum;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

import static com.wuhunyu.rpc.common.constants.CommonConstant.*;

/**
 * 消息编解码器
 *
 * @author wuhunyu
 * @version 1.0
 * @date 2022-09-03 16:41
 */

@Slf4j
@ChannelHandler.Sharable
public class MessageCodec extends MessageToMessageCodec<ByteBuf, Message> {

    /**
     * 魔数
     */
    private static final byte[] MAGIC_NUM = {'w', 'u', 'h', 'u', 'n', 'y', 'u'};

    /**
     * 无效字符填充
     */
    private static final byte[] INVALID_BYTES = {'0', '0', '0', '0', '0', '0', '0'};

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message message, List<Object> list) throws Exception {
        ByteBuf byteBuf = channelHandlerContext.alloc().buffer();
        // 7 魔数
        byteBuf.writeBytes(MAGIC_NUM);
        // 1 version
        byteBuf.writeByte(ConfigProperties.getPropertyOrDefault(VERSION, Byte.class, DEFAULT_VERSION));
        // 1 序列化
        Byte serializeType = message.getSerializeType();
        byteBuf.writeByte(serializeType);
        // 8 序列id
        byteBuf.writeLong(message.getSequenceId());
        // 4 消息类型
        byteBuf.writeInt(message.getMessageType());
        // 序列化
        SerializeTypeEnum serializeTypeEnum = SerializeTypeEnum.findSerializeTypeByType(serializeType);
        if (serializeTypeEnum == null) {
            throw new IllegalStateException("序列化方式不存在, serializeType: " + serializeType);
        }
        byte[] bytes = serializeTypeEnum.serialize(message);
        // 4 内容长度
        byteBuf.writeInt(bytes.length);
        // 7 无效字符填充
        byteBuf.writeBytes(INVALID_BYTES);
        // 内容
        byteBuf.writeBytes(bytes);
        list.add(byteBuf);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        // 7 魔数
        byte[] bytes = new byte[MAGIC_NUM.length];
        byteBuf.readBytes(bytes);
        // 魔数不一致，丢弃消息
        if (!this.checkMagicNum(bytes)) {
            log.info("魔数不一致，magicNum: {}", Arrays.toString(bytes));
            return;
        }
        // 1 version
        byte version = byteBuf.readByte();
        // 版本不一致，丢弃消息
        if (version != ConfigProperties.getPropertyOrDefault(CommonConstant.VERSION, Byte.class, (byte) 1)) {
            log.info("版本不一致，version: {}", version);
            return;
        }
        // 1 序列化
        byte serializeType = byteBuf.readByte();
        SerializeTypeEnum serializeTypeEnum = SerializeTypeEnum.findSerializeTypeByType(serializeType);
        // 序列化方式不存在，丢弃消息
        if (serializeTypeEnum == null) {
            log.info("序列化方式不存在，serializeType: {}", serializeType);
            return;
        }
        // 8 序列id
        long sequenceId = byteBuf.readLong();
        // 4 消息类型
        int messageType = byteBuf.readInt();
        Class<? extends Message> messageTypeClass = MessageTypeEnum.findMessageTypeByType(messageType);
        if (messageTypeClass == null) {
            log.info("消息类型不存在，messageType: {}", messageType);
            return;
        }
        // 4 内容长度
        int len = byteBuf.readInt();
        // 7 无效字符填充
        byteBuf.skipBytes(INVALID_BYTES.length);
        // 内容
        byte[] content = new byte[len];
        byteBuf.readBytes(content);
        // 反序列化
        list.add(serializeTypeEnum.deSerialize(content, messageTypeClass));
    }

    /**
     * 魔数比较
     *
     * @param bytes 魔数
     * @return 是否相等(true : 相等 ; false : 不相等)
     */
    private boolean checkMagicNum(byte[] bytes) {
        int len = MAGIC_NUM.length;
        for (int i = 0; i < len; i++) {
            if (MAGIC_NUM[i] != bytes[i]) {
                return false;
            }
        }
        return true;
    }

}
