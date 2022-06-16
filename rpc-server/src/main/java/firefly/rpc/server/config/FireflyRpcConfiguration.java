package firefly.rpc.server.config;

import firefly.rpc.core.FireflyRpcProps;
import firefly.rpc.registry.RegistryType;
import firefly.rpc.server.RpcServer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(FireflyRpcProps.class)
@RequiredArgsConstructor
public class FireflyRpcConfiguration {
    
    private final FireflyRpcProps rpcProps;
    
    @Bean
    public RpcServer init() throws Exception {
        RegistryType type = RegistryType.valueOf(rpcProps.getRegistryType());
        
        return new RpcServer(rpcProps.getServicePort(), null);
    }
}
