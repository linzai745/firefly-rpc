package firefly.rpc.core;

import lombok.Data;

import java.io.Serializable;

@Data
public class FireflyRpcRequest implements Serializable {
    private String serviceVersion;
    private String className;
    private String methodName;
    private Object[] params;
    private Class<?>[] parameterTypes;
}
