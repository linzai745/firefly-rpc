package firefly.rpc.serialization;

public class SerializationException extends RuntimeException {
    
    public SerializationException() {
        super();
    }
    
    public SerializationException(String msg) {
        super(msg);
    }
    
    public SerializationException(String msg, Throwable cause) {
        super(msg, cause);
    }
    
    public SerializationException(Throwable cause) {
        super(cause);
    }
}
