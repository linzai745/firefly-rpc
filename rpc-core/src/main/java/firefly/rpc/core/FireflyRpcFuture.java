package firefly.rpc.core;

import io.netty.util.concurrent.Promise;
import lombok.Data;

@Data
public class FireflyRpcFuture<T> {
    private Promise<T> promise;
    private long timeout;
    
    public FireflyRpcFuture(Promise<T> promise, long timeout) {
        this.promise = promise;
        this.timeout = timeout;
    }
}
