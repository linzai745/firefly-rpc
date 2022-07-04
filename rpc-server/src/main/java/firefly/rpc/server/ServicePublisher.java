package firefly.rpc.server;

import firefly.rpc.codec.FireflyRpcDecoder;
import firefly.rpc.codec.FireflyRpcEncoder;
import firefly.rpc.core.ServiceHelper;
import firefly.rpc.core.ServiceMeta;
import firefly.rpc.handler.FireflyRequestHandler;
import firefly.rpc.registry.RegistryService;
import firefly.rpc.server.annotation.RpcService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ServicePublisher implements InitializingBean, BeanPostProcessor, ApplicationContextAware {
    
    private String serverAddress;
    private final int serverPort;
    private final RegistryService serviceRegistry;
    private ApplicationContext context;
    
    private final Map<String, Object> rpcServiceMap = new HashMap<>();
    
    public ServicePublisher(int serverPort, RegistryService serviceRegistry) {
        this.serverPort = serverPort;
        this.serviceRegistry = serviceRegistry;
    }
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        new Thread(() -> {
            try {
                startServer();
            } catch (Exception e) {
                log.error("start rpc server error.", e);
            }
        }).start();
    }
    
    /**
     * service key conclude applicationName:serviceSimpleName:serviceVersion:#num
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(RpcService.class)) {
            log.info("register service {}.", beanName);
            String applicationId = context.getId();
            RpcService rpcService = bean.getClass().getAnnotation(RpcService.class);
            String serviceName = rpcService.serviceInterface().getSimpleName();
            String className = rpcService.serviceInterface().getName();
            String serviceVersion = rpcService.serviceVersion();
    
            ServiceMeta serviceMeta = ServiceMeta.builder()
                    .serviceAddr(this.serverAddress)
                    .servicePort(this.serverPort)
                    .applicationId(applicationId)
                    .serviceName(serviceName)
                    .className(className)
                    .serviceVersion(serviceVersion)
                    .build();
            try {
                serviceRegistry.register(serviceMeta);
                rpcServiceMap.put(ServiceHelper.buildServiceKey(applicationId, serviceName, serviceVersion), bean);
            } catch (Exception e) {
                log.error("failed to register service {}#{}", serviceName, serviceVersion, e);
            }
        }
        return bean;
    }
    
    private void startServer() throws Exception {
        this.serverAddress = InetAddress.getLocalHost().getHostAddress();
        
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            serverBootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new FireflyRpcEncoder())
                                    .addLast(new FireflyRpcDecoder())
                                    .addLast(new FireflyRequestHandler(rpcServiceMap));
                        }
                    })
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
        
            ChannelFuture channelFuture = serverBootstrap.bind(this.serverAddress, this.serverPort).sync();
            log.info("firefly server address {} started on port {}", this.serverAddress, this.serverPort);
            channelFuture.channel().closeFuture().sync();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
