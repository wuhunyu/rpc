package com.wuhunyu.rpc.server;

import com.alibaba.nacos.api.exception.NacosException;
import com.wuhunyu.rpc.common.codec.MessageCodec;
import com.wuhunyu.rpc.common.config.ConfigProperties;
import com.wuhunyu.rpc.common.constants.NacosConstant;
import com.wuhunyu.rpc.common.decoder.SelfLengthFieldBasedFrameDecoder;
import com.wuhunyu.rpc.common.handler.PingHandler;
import com.wuhunyu.rpc.common.handler.PongHandler;
import com.wuhunyu.rpc.common.utils.NacosUtil;
import com.wuhunyu.rpc.server.constants.ServerConstant;
import com.wuhunyu.rpc.server.handler.ConnectionAndExceptionHandler;
import com.wuhunyu.rpc.server.handler.IdleHandler;
import com.wuhunyu.rpc.server.handler.RequestMessageHandler;
import com.wuhunyu.rpc.server.handler.RpcServerHandler;
import com.wuhunyu.rpc.server.model.NacosConfigModel;
import com.wuhunyu.rpc.server.model.RpcServerModel;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ConnectException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.wuhunyu.rpc.server.constants.ServerConstant.*;

/**
 * rpc 服务端
 *
 * @author wuhunyu
 * @version 1.0
 * @date 2022-09-05 11:17
 */

@Slf4j
public final class RpcServerApplication {

    /**
     * 一次性最大读取长度
     */
    private static final int MAX_READ_LENGTH = 1024 * 10;

    /**
     * 空闲读秒数
     */
    private static final int IDLE_READ_SECOND = 5;

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
     * 连接 & 全局异常 处理
     */
    private static final ConnectionAndExceptionHandler CONNECTION_AND_EXCEPTION_HANDLER
            = new ConnectionAndExceptionHandler();

    /**
     * 请求调用事件
     */
    private static final RequestMessageHandler REQUEST_MESSAGE_HANDLER = new RequestMessageHandler();

    static {
        // 初始化
        try {
            RpcServerApplication.init();
        } catch (IOException | NacosException e) {
            log.warn("服务端初始化失败: {}", e.getLocalizedMessage(), e);
        }
    }

    /**
     * 初始化配置
     *
     * @throws IOException    IOException
     * @throws NacosException NacosException
     */
    private static void init() throws IOException, NacosException {
        // 读取 nacos 配置
        NacosConfigModel.initProperties();
        // 从nacos配置中新获取配置信息
        String configStr = NacosUtil.findConfig(ServerConstant.CONFIG_LOCATION,
                ConfigProperties.getPropertyOrDefault(NacosConstant.GROUP, String.class, NacosConstant.DEFAULT_GROUP));
        // 解析 服务端配置
        RpcServerModel.initProperties(configStr);
        // 将自己注册到 nacos
        NacosUtil.registerServer(ServerConstant.SERVER_NAME,
                ConfigProperties.getProperty(EXPOSE_GROUP, String.class),
                ConfigProperties.getProperty(EXPOSE_IP, String.class),
                ConfigProperties.getProperty(EXPOSE_PORT, Integer.class),
                ConfigProperties.getProperty(EXPOSE_WEIGHT, Double.class));
        // 扫描指定包路径
        RpcServerHandler.scanAllServer(ConfigProperties.getProperty(ServerConstant.RPC_SERVER_SCAN, String.class));
    }

    /**
     * 建立连接，并返回 channel
     *
     * @return channel
     * @throws TimeoutException TimeoutException
     */
    public static Channel of() throws TimeoutException {
        NioEventLoopGroup connectionEventLoopGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workEventLoopGroup = new NioEventLoopGroup(4);
        ChannelFuture channelFuture = new ServerBootstrap()
                .group(connectionEventLoopGroup, workEventLoopGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) {
                        ChannelPipeline pipeline = nioSocketChannel.pipeline();

                        // 空闲检查
                        pipeline.addLast(new IdleStateHandler(IDLE_READ_SECOND, 0, 0));
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
                        pipeline.addLast(REQUEST_MESSAGE_HANDLER);
                        // 连接断开事件 & 异常处理事件
                        pipeline.addLast(CONNECTION_AND_EXCEPTION_HANDLER);
                    }
                })
                .bind(ConfigProperties.getPropertyOrDefault(EXPOSE_PORT, Integer.class, 1000));

        Promise<Channel> promise = new DefaultPromise<>(workEventLoopGroup.next());
        // 连接建立事件
        channelFuture.addListener((connectionFuture -> {
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
                            connectionEventLoopGroup.shutdownGracefully();
                            workEventLoopGroup.shutdownGracefully();
                        }
                    });
            promise.setSuccess(channel);
        }));

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
