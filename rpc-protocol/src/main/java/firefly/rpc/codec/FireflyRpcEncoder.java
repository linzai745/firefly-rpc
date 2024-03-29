package firefly.rpc.codec;

import firefly.rpc.protocol.FireflyRpcProtocol;
import firefly.rpc.protocol.MsgHeader;
import firefly.rpc.serialization.FireflyRpcSerialization;
import firefly.rpc.serialization.SerializationFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;


public class FireflyRpcEncoder extends MessageToByteEncoder<FireflyRpcProtocol<Object>> {
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
    protected void encode(ChannelHandlerContext ctx, FireflyRpcProtocol<Object> msg, ByteBuf byteBuf) throws Exception {
        MsgHeader header = msg.getHeader();
        byteBuf.writeShort(header.getMagic());
        byteBuf.writeByte(header.getVersion());
        byteBuf.writeByte(header.getSerialization());
        byteBuf.writeByte(header.getMsgType());
        byteBuf.writeByte(header.getStatus());
        byteBuf.writeLong(header.getRequestId());
        FireflyRpcSerialization serialization = SerializationFactory.getSerialization(header.getSerialization());
        byte[] data = serialization.serialize(msg.getBody());
        byteBuf.writeInt(data.length);
        byteBuf.writeBytes(data);
    }
}
