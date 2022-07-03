package firefly.rpc.serialization;

public class KryoSerialization implements FireflyRpcSerialization {
    @Override
    public <T> byte[] serialize(T obj) {
        return new byte[0];
    }
    
    @Override
    public <T> T deserialize(byte[] data, Class<T> clz) {
        return null;
    }
}
