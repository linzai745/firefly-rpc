package firefly.rpc.core;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "rpc")
public class FireflyRpcProps {
    private int servicePort;
    private String registryAddr;
    private String registryType;
}
