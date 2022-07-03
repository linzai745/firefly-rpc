package firefly.rpc.protocol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MsgHeader implements Serializable {
    /**
     * magic number use 2 byte, suite for short
     */
    private short magic;
    
    /**
     * version number use 1 byte,
     */
    private byte version;
    
    /**
     * serialization type use 1 byte
     */
    private byte serialization;
    
    /**
     *  request or response, include other, use 1 byte
     */
    private byte msgType;
    
    /**
     * status, use 1 byte
     */
    private byte status;
    
    /**
     * requestId, mark request, help response, use 8 byte , maximum 2^64
     */
    private long requestId;
    
    /**
     * message body length, no limit
     */
    private int msgLen;
}
