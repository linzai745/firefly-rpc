package firefly.rpc.core.exception;

public enum ExceptionType {
    SERVICE_NOT_FOUND("service not found")
    ;
    private final String msg;
    
    ExceptionType(String msg) {
        this.msg = msg;
    }
    
    public String getMsg() {
        return msg;
    }
}
