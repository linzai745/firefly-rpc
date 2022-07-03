package firefly.rpc.serialization;

import lombok.Getter;

public enum SerializationTypeEnum {
    HESSIAN(0x10),
    JSON(0x20)
    ;
    
    @Getter
    private final int type;
    
    SerializationTypeEnum(int type) {
        this.type = type;
    }
    
    public static SerializationTypeEnum findByType(byte serializationType) {
        SerializationTypeEnum[] typeEnums = SerializationTypeEnum.values();
        for (SerializationTypeEnum typeEnum : typeEnums) {
            if (typeEnum.getType() == serializationType) {
                return typeEnum;
            }
        }
        
        return HESSIAN;
    }
}
