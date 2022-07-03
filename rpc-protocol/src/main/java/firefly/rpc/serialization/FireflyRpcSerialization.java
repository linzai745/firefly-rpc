package firefly.rpc.serialization;

import java.io.IOException;

public interface FireflyRpcSerialization {
    
    <T> byte[] serialize(T obj) throws IOException;
    
    <T> T deserialize(byte[] data, Class<T> clz) throws IOException;
}
