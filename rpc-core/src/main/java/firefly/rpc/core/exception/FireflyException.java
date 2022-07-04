package firefly.rpc.core.exception;

public class FireflyException extends RuntimeException {
    
    public FireflyException() {
        super();
    }
    
    public FireflyException(String msg) {
        super(msg);
    }
    
    public FireflyException(String msg, Throwable cause) {
        super(msg, cause);
    }
    
    public FireflyException(Throwable cause) {
        super(cause);
    }

    public FireflyException(ExceptionType type) {
        super(type.getMsg());
    }
}
