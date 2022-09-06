package com.wuhunyu.rpc.client;

import com.alibaba.nacos.api.exception.NacosException;
import com.wuhunyu.rpc.client.constants.ClientConstant;
import com.wuhunyu.rpc.client.handler.ConnectionAndExceptionHandler;
import com.wuhunyu.rpc.client.handler.IdleHandler;
import com.wuhunyu.rpc.client.handler.ResponseMessageHandler;
import com.wuhunyu.rpc.client.model.NacosConfigModel;
import com.wuhunyu.rpc.client.model.RpcClientModel;
import com.wuhunyu.rpc.common.codec.MessageCodec;
import com.wuhunyu.rpc.common.config.ConfigProperties;
import com.wuhunyu.rpc.common.constants.NacosConstant;
import com.wuhunyu.rpc.common.decoder.SelfLengthFieldBasedFrameDecoder;
import com.wuhunyu.rpc.common.handler.PingHandler;
import com.wuhunyu.rpc.common.handler.PongHandler;
import com.wuhunyu.rpc.common.utils.NacosUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ConnectException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 客户端 服务
 *
 * @author wuhunyu
 * @version 1.0
 * @date 2022-09-05 14:40
 */

@Slf4j
public class RpcClientApplication {

    /**
     * 一次性最大读取长度
     */
    private static final int MAX_READ_LENGTH = 1024 * 10;

    /**
     * 空闲写秒数
     */
    private static final int IDLE_WRITE_SECOND = 3;

    /**
     * 空闲检测机制
     */
    private static final IdleHandler IDLE_HANDLER = new IdleHandler();

    /**
     * ping处理
     */
    private static final PingHandler PING_HANDLER = new PingHandler();

    /**
     * pong处理
     */
    private static final PongHandler PONG_HANDLER = new PongHandler();

    /**
     * 消息编解码器
     */
    private static final MessageCodec MESSAGE_CODEC = new MessageCodec();

    /**
     * 消息响应处理器
     */
    private static final ResponseMessageHandler RESPONSE_MESSAGE_HANDLER = new ResponseMessageHandler();

    /**
     * 连接 & 全局异常 处理
     */
    private static final ConnectionAndExceptionHandler CONNECTION_AND_EXCEPTION_HANDLER
            = new ConnectionAndExceptionHandler();

    static {
        // 初始化
        try {
            init();
        } catch (IOException | NacosException e) {
            log.warn("客户端初始化失败: {}", e.getLocalizedMessage(), e);
        }
    }

    /**
     * 初始化客户端配置
     *
     * @throws IOException    IOException
     * @throws NacosException NacosException
     */
    public static void init() throws IOException, NacosException {
        // 读取 nacos 配置
        NacosConfigModel.initProperties();
        // 从nacos配置中新获取配置信息
        String configStr = NacosUtil.findConfig(ClientConstant.CONFIG_LOCATION,
                ConfigProperties.getPropertyOrDefault(NacosConstant.GROUP, String.class, NacosConstant.DEFAULT_GROUP));
        // 解析 客户端配置
        RpcClientModel.initProperties(configStr);
    }

    /**
     * 连接服务端
     *
     * @param serverAddr 服务端地址
     * @param port       服务端端口
     * @return channel
     */
    public static Channel of(String serverAddr, int port) throws TimeoutException {
        // 工作线程
        NioEventLoopGroup workEventLoopGroup = new NioEventLoopGroup(10);
        ChannelFuture channelFuture = new Bootstrap()
                .group(workEventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        ChannelPipeline pipeline = nioSocketChannel.pipeline();

                        // 空闲检查
                        pipeline.addLast(new IdleStateHandler(0, IDLE_WRITE_SECOND, 0));
                        // 帧 解码器
                        pipeline.addLast(new SelfLengthFieldBasedFrameDecoder(MAX_READ_LENGTH));
                        // 心跳检测
                        pipeline.addLast(IDLE_HANDLER);
                        // ping处理
                        pipeline.addLast(PING_HANDLER);
                        // pong处理
                        pipeline.addLast(PONG_HANDLER);
                        // 消息编解码器
                        pipeline.addLast(MESSAGE_CODEC);
                        // 请求事件处理
                        pipeline.addLast(RESPONSE_MESSAGE_HANDLER);
                        // 连接断开事件 & 异常处理事件
                        pipeline.addLast(CONNECTION_AND_EXCEPTION_HANDLER);
                    }
                })
                .connect(serverAddr, port);

        Promise<Channel> promise = new DefaultPromise<>(workEventLoopGroup.next());
        channelFuture.addListener((connectionFuture) -> {
            // 连接建立
            if (!connectionFuture.isSuccess()) {
                promise.setFailure(new ConnectException("连接建立失败"));
                return;
            }
            Channel channel = channelFuture.channel();
            // channel 关闭事件监听
            channel.closeFuture()
                    .addListener(closeFuture -> {
                        if (closeFuture.isSuccess()) {
                            // 关闭线程池
                            log.info("服务关闭ing");
                            workEventLoopGroup.shutdownGracefully();
                        }
                    });
            promise.setSuccess(channel);
        });

        // 等待最多3秒
        try {
            promise.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new TimeoutException("连接建立超时");
        }
        if (!promise.isSuccess()) {
            throw new TimeoutException("连接建立超时");
        }
        return promise.getNow();
    }

}
