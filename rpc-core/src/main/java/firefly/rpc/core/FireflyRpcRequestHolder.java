package firefly.rpc.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class FireflyRpcRequestHolder {
    public final static AtomicLong REQUEST_ID_GEN = new AtomicLong(0);
    
    public static final Map<Long, FireflyRpcFuture<FireflyRpcResponse>> REQUEST_MAP = new ConcurrentHashMap<>();
}
