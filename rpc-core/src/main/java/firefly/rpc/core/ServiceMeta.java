package firefly.rpc.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceMeta {
    
    private String serviceName;
    
    private String serviceVersion;
    
    private String serviceAddr;
    
    private int servicePort;
    
}
