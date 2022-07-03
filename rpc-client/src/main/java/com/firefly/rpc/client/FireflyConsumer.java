package com.firefly.rpc.client;

import firefly.rpc.codec.FireflyRpcDecoder;
import firefly.rpc.codec.FireflyRpcEncoder;
import firefly.rpc.core.FireflyRpcRequest;
import firefly.rpc.core.ServiceHelper;
import firefly.rpc.core.ServiceMeta;
import firefly.rpc.handler.FireflyResponseHandler;
import firefly.rpc.protocol.FireflyRpcProtocol;
import firefly.rpc.registry.RegistryService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FireflyConsumer {
    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;

    public FireflyConsumer() {
        bootstrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup(4);
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new FireflyRpcEncoder())
                                .addLast(new FireflyRpcDecoder())
                                .addLast(new FireflyResponseHandler());
                    }
                });
    }
    
    public void sendRequest(FireflyRpcProtocol<FireflyRpcRequest> protocol, RegistryService registryService) throws Exception {
        FireflyRpcRequest request = protocol.getBody();
        Object[] params = request.getParams();
        String serviceKey = ServiceHelper.buildServiceKey(request.getClassName(), request.getServiceVersion());
        
        int invokeHashCode = params.length > 0 ? params[0].hashCode() : serviceKey.hashCode();
        ServiceMeta serviceMetaData = registryService.discovery(serviceKey, invokeHashCode);
        
        if (serviceMetaData != null) {
            ChannelFuture future = bootstrap.connect(serviceMetaData.getServiceAddr(), serviceMetaData.getServicePort()).sync();
            future.addListener((ChannelFutureListener) arg0 -> {
                if (future.isSuccess()) {
                    log.info("connect rpc service {} on port {} success.", serviceMetaData.getServiceAddr(), serviceMetaData.getServicePort());
                } else {
                    log.error("connect rpc server {} on port {} failed.", serviceMetaData.getServiceAddr(), serviceMetaData.getServicePort());
                    future.cause().printStackTrace();
                    eventLoopGroup.shutdownGracefully();
                }
            });
            future.channel().writeAndFlush(protocol);
        }
    }
}
