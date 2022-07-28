package firefly.rpc.serialization;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.SerializerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class HessianSerialization implements FireflyRpcSerialization {
    @Override
    public <T> byte[] serialize(T obj) {
        if (obj == null)
            throw new NullPointerException();
        byte[] result;
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            Hessian2Output out = new Hessian2Output(os);
            out.setSerializerFactory(new SerializerFactory());
            out.startMessage();
            out.writeObject(obj);
            out.flush();
            out.completeMessage();
            result = os.toByteArray();
        } catch (Exception e) {
            throw new SerializationException(e);
        }
        return result;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialize(byte[] data, Class<T> clz) {
        if (data == null)
            throw new NullPointerException();
        T result;
        try (ByteArrayInputStream is = new ByteArrayInputStream(data)) {
            Hessian2Input in = new Hessian2Input(is);
            in.setSerializerFactory(new SerializerFactory());
            in.startMessage();
            result = (T) in.readObject(clz);
            in.completeMessage();
    
        } catch (Exception e) {
            throw new SerializationException(e);
        }
        return result;
    }
}
