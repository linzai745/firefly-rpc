package firefly.rpc.protocol;

import lombok.Getter;

public enum MsgType {
    REQUEST(1),
    RESPONSE(2),
    HEARTBEAT(3)
    ;
    
    @Getter
    private final int type;
    
    MsgType(int type) {
        this.type = type;
    }
    
    public static MsgType findByType(int type) {
        MsgType[] msgTypes = MsgType.values();
        for (MsgType msgType : msgTypes) {
            if (msgType.getType() == type) {
                return msgType;
            }
        }
        return null;
    }
}
