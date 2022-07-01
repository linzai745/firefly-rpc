package firefly.rpc.server.config;

import firefly.rpc.core.FireflyRpcProps;
import firefly.rpc.registry.RegistryFactory;
import firefly.rpc.registry.RegistryService;
import firefly.rpc.registry.RegistryType;
import firefly.rpc.server.ServicePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(FireflyRpcProps.class)
@RequiredArgsConstructor
public class FireflyRpcConfiguration {
    
    private final FireflyRpcProps rpcProps;
    
    /**
     * 这里应该加入Spring Boot Condition Bean
     * @return
     * @throws Exception
     */
    @Bean
    public ServicePublisher servicePublisher() throws Exception {
        RegistryType type = RegistryType.valueOf(rpcProps.getRegistryType());
        RegistryService registryService = RegistryFactory.getInstance(rpcProps.getRegistryAddr(), type);
        return new ServicePublisher(rpcProps.getServicePort(), registryService);
    }
}
