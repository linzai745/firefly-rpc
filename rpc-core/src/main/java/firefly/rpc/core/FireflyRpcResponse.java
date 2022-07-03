package firefly.rpc.core;

import lombok.Data;

import java.io.Serializable;

@Data
public class FireflyRpcResponse implements Serializable {
    private Object data;
    private String message;
}
