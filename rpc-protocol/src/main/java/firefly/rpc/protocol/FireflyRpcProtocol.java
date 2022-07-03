package firefly.rpc.protocol;

import lombok.Data;

import java.io.Serializable;

@Data
public class FireflyRpcProtocol<T> implements Serializable {
    private MsgHeader header; // protocol header
    private T body; // protocol body
}
