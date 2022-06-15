package firefly.rpc.server;

import firefly.rpc.core.ServiceMeta;
import firefly.rpc.registry.RegistryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RpcServer implements InitializingBean, BeanPostProcessor {
    
    private String serverAddress;
    private final int serverPort;
    private RegistryService serviceRegistry;
    
    private final Map<String, Object> rpcServiceMap = new HashMap<>();
    
    public RpcServer(int serverPort, RegistryService serviceRegistry) {
        this.serverPort = serverPort;
        this.serviceRegistry = serviceRegistry;
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
    
    }
    
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
