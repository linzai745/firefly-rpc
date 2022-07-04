package firefly.rpc.handler;

import firefly.rpc.core.FireflyRpcRequest;
import firefly.rpc.core.FireflyRpcResponse;
import firefly.rpc.core.ServiceHelper;
import firefly.rpc.protocol.FireflyRpcProtocol;
import firefly.rpc.protocol.MsgHeader;
import firefly.rpc.protocol.MsgStatus;
import firefly.rpc.protocol.MsgType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.reflect.FastClass;

import java.util.Map;

@Slf4j
public class FireflyRequestHandler extends SimpleChannelInboundHandler<FireflyRpcProtocol<FireflyRpcRequest>> {
    
    private final Map<String, Object> rpcServiceMap;
    
    public FireflyRequestHandler(Map<String, Object> rpcServiceMap) {
        this.rpcServiceMap = rpcServiceMap;
    }
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FireflyRpcProtocol<FireflyRpcRequest> protocol) throws Exception {
        FireflyRpcRequestProcessor.submitRequest(() -> {
            FireflyRpcProtocol<FireflyRpcResponse> resProtocol = new FireflyRpcProtocol<>();
            FireflyRpcResponse response = new FireflyRpcResponse();
            MsgHeader header = protocol.getHeader();
            header.setMsgType((byte) MsgType.RESPONSE.getType());
            resProtocol.setHeader(header);
            resProtocol.setBody(response);
            try {
                Object result = handle(protocol.getBody());
                response.setData(result);
                header.setStatus((byte) MsgStatus.SUCCESS.getCode());
            } catch (Throwable throwable) {
                header.setStatus((byte) MsgStatus.FAIL.getCode());
                response.setMessage(throwable.toString());
                log.error("process request {} error", header.getRequestId(), throwable);
            }
            ctx.writeAndFlush(resProtocol);
        });
    }
    
    private Object handle(FireflyRpcRequest request) throws Throwable {
        String serviceKey = ServiceHelper.buildServiceKey(request.getApplicationName(), request.getServiceName(), request.getServiceVersion());
        Object serviceBean = rpcServiceMap.get(serviceKey);
        if (serviceBean == null)
            throw new RuntimeException(String.format("service not exist: %s:%s", request.getClassName(), request.getMethodName()));
        Class<?> serviceClass = serviceBean.getClass();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] params = request.getParams();
        FastClass fastClass = FastClass.create(serviceClass);
        int methodIndex = fastClass.getIndex(methodName, parameterTypes);
        return fastClass.invoke(methodIndex, serviceBean, params);
    }
}
