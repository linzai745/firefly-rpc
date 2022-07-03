package com.firefly.rpc.client;

import firefly.rpc.core.FireflyRpcFuture;
import firefly.rpc.core.FireflyRpcRequest;
import firefly.rpc.core.FireflyRpcRequestHolder;
import firefly.rpc.core.FireflyRpcResponse;
import firefly.rpc.protocol.*;
import firefly.rpc.registry.RegistryService;
import firefly.rpc.serialization.SerializationTypeEnum;
import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultPromise;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

public class FireflyInvokerProxy implements InvocationHandler {
    
    private final String serviceVersion;
    private final long timeout;
    private final RegistryService registryService;
    
    public FireflyInvokerProxy(String serviceVersion, long timeout, RegistryService registryService) {
        this.serviceVersion = serviceVersion;
        this.timeout = timeout;
        this.registryService = registryService;
    }
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // Build RPC Request protocol
        long requestId = FireflyRpcRequestHolder.REQUEST_ID_GEN.incrementAndGet();
        MsgHeader header = MsgHeader.builder()
                .magic(ProtocolConstants.MAGIC)
                .version(ProtocolConstants.VERSION)
                .requestId(requestId)
                .serialization((byte) SerializationTypeEnum.HESSIAN.getType())
                .msgType((byte) MsgType.REQUEST.getType())
                .status((byte) 0x1)
                .build();
    
        FireflyRpcRequest request = FireflyRpcRequest.builder()
                .serviceVersion(this.serviceVersion)
                .className(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .params(args)
                .build();
    
        FireflyRpcProtocol<FireflyRpcRequest> protocol = new FireflyRpcProtocol<>();
        protocol.setHeader(header);
        protocol.setBody(request);
        
        // send request
        FireflyConsumer consumer = new FireflyConsumer();
        FireflyRpcFuture<FireflyRpcResponse> future = new FireflyRpcFuture<>(new DefaultPromise<>(new DefaultEventLoop()), timeout);
        FireflyRpcRequestHolder.REQUEST_MAP.put(requestId, future);
        consumer.sendRequest(protocol, this.registryService);
        return future.getPromise().get(future.getTimeout(), TimeUnit.SECONDS).getData();
    }
}
