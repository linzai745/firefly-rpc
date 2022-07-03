package firefly.rpc.handler;

import firefly.rpc.core.FireflyRpcFuture;
import firefly.rpc.core.FireflyRpcRequestHolder;
import firefly.rpc.core.FireflyRpcResponse;
import firefly.rpc.protocol.FireflyRpcProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class FireflyResponseHandler extends SimpleChannelInboundHandler<FireflyRpcProtocol<FireflyRpcResponse>> {
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FireflyRpcProtocol<FireflyRpcResponse> msg) throws Exception {
        long requestId = msg.getHeader().getRequestId();
        FireflyRpcFuture<FireflyRpcResponse> future = FireflyRpcRequestHolder.REQUEST_MAP.remove(requestId);
        future.getPromise().setSuccess(msg.getBody());
    }
}
