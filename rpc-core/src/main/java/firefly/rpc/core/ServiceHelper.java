package firefly.rpc.core;

public class ServiceHelper {
    public static String buildServiceKey(String applicationId, String serviceName, String serviceVersion) {
        String serviceKey = String.join(":", applicationId, serviceName);
        return String.join("#", serviceKey, serviceVersion);
    }
}
