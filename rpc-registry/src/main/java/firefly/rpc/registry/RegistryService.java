package firefly.rpc.registry;

import firefly.rpc.core.ServiceMeta;

import java.io.IOException;

public interface RegistryService {
    
    void register(ServiceMeta serviceMeta) throws Exception;
    
    void unRegister(ServiceMeta serviceMeta) throws Exception;
    
    ServiceMeta discovery(String serviceName, int invokeHashCode) throws Exception;
    
    void destroy() throws IOException;
}
