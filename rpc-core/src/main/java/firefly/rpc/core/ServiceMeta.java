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
    
    private String applicationId;
    
    private String serviceName;
    
    private String className;
    
    private String serviceVersion;
    
    private String serviceAddr;
    
    private int servicePort;
    
}
