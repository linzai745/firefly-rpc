package firefly.rpc.codec;

import firefly.rpc.core.FireflyRpcRequest;
import firefly.rpc.core.FireflyRpcResponse;
import firefly.rpc.protocol.FireflyRpcProtocol;
import firefly.rpc.protocol.MsgHeader;
import firefly.rpc.protocol.MsgType;
import firefly.rpc.protocol.ProtocolConstants;
import firefly.rpc.serialization.FireflyRpcSerialization;
import firefly.rpc.serialization.SerializationFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class FireflyRpcDecoder extends ByteToMessageDecoder {
    /*
    +---------------------------------------------------------------+
    | 魔数 2byte | 协议版本号 1byte | 序列化算法 1byte | 报文类型 1byte  |
    +---------------------------------------------------------------+
    | 状态 1byte |        消息 ID 8byte     |      数据长度 4byte     |
    +---------------------------------------------------------------+
    |                   数据内容 （长度不定）                          |
    +---------------------------------------------------------------+
    */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < ProtocolConstants.HEADER_TOTAL_LEN)
            return;
        in.markReaderIndex();
        
        // read magic number
        short magic = in.readShort();
        if (magic != ProtocolConstants.MAGIC)
            throw new IllegalArgumentException("magic number is illegal, " + magic);
    
        byte version = in.readByte();
        byte serializeType = in.readByte();
        byte msgType = in.readByte();
        byte status = in.readByte();
        byte requestId = in.readByte();
        byte dataLength = in.readByte();
        
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }
    
        byte[] data = new byte[dataLength];
        in.readBytes(data);
    
        MsgType msgTypeEnum = MsgType.findByType(msgType);
        if (msgTypeEnum == null)
            return;
    
        MsgHeader header = MsgHeader.builder()
                .magic(magic)
                .version(version)
                .serialization(serializeType)
                .msgType(msgType)
                .status(status)
                .requestId(requestId)
                .msgLen(dataLength)
                .build();
    
        FireflyRpcSerialization serialization = SerializationFactory.getSerialization(serializeType);
        switch (msgTypeEnum) {
            case REQUEST:
                FireflyRpcRequest request = serialization.deserialize(data, FireflyRpcRequest.class);
                if (request != null) {
                    FireflyRpcProtocol<FireflyRpcRequest> protocol = new FireflyRpcProtocol<>();
                    protocol.setHeader(header);
                    protocol.setBody(request);
                    out.add(protocol);
                }
                break;
            case RESPONSE:
                FireflyRpcResponse response = serialization.deserialize(data, FireflyRpcResponse.class);
                if (response != null) {
                    FireflyRpcProtocol<FireflyRpcResponse> protocol = new FireflyRpcProtocol<>();
                    protocol.setHeader(header);
                    protocol.setBody(response);
                    out.add(protocol);
                }
                break;
            case HEARTBEAT:
                // TODO
                break;
        }
    }
}
