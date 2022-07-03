package firefly.rpc.serialization;

public class SerializationFactory {
    
    public static FireflyRpcSerialization getSerialization(byte serializationType) {
        SerializationTypeEnum type = SerializationTypeEnum.findByType(serializationType);
        switch (type) {
            case HESSIAN:
                return new HessianSerialization();
            default:
                throw new IllegalArgumentException("serialization type is illegal, " + serializationType);
        }
    }
}
