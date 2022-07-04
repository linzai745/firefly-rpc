package firefly.rpc.registry;

import firefly.rpc.core.ServiceHelper;
import firefly.rpc.core.ServiceMeta;
import firefly.rpc.core.exception.ExceptionType;
import firefly.rpc.core.exception.FireflyException;
import firefly.rpc.registry.loadbalancer.ZKConsistentHashLoadBalancer;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

@Slf4j
public class ZookeeperRegistryService implements RegistryService {
    private static final int BASE_SLEEP_TIME_MS = 1000;
    
    private static final int MAX_RETRIES = 3;
    
    private static final String ZK_BASE_PATH = "/firefly_rpc";
    
    private final ServiceDiscovery<ServiceMeta> serviceDiscovery;
    
    public ZookeeperRegistryService(String registryAddr) throws Exception {
        CuratorFramework client = CuratorFrameworkFactory.newClient(registryAddr, new ExponentialBackoffRetry(BASE_SLEEP_TIME_MS, MAX_RETRIES));
        client.start();
        JsonInstanceSerializer<ServiceMeta> serializer = new JsonInstanceSerializer<>(ServiceMeta.class);
        this.serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceMeta.class)
                .basePath(ZK_BASE_PATH)
                .client(client)
                .serializer(serializer)
                .build();
        this.serviceDiscovery.start();
    }
    
    @Override
    public void register(ServiceMeta serviceMeta) throws Exception {
        ServiceInstance<ServiceMeta> serviceInstance = ServiceInstance.<ServiceMeta>builder()
                .name(ServiceHelper.buildServiceKey(serviceMeta.getApplicationId(), serviceMeta.getServiceName(), serviceMeta.getServiceVersion()))
                .address(serviceMeta.getServiceAddr())
                .port(serviceMeta.getServicePort())
                .payload(serviceMeta)
                .build();
        serviceDiscovery.registerService(serviceInstance);
    }
    
    @Override
    public void unRegister(ServiceMeta serviceMeta) throws Exception {
        ServiceInstance<ServiceMeta> serviceInstance = ServiceInstance.<ServiceMeta>builder()
                .name(ServiceHelper.buildServiceKey(serviceMeta.getApplicationId(), serviceMeta.getServiceName(), serviceMeta.getServiceVersion()))
                .address(serviceMeta.getServiceAddr())
                .port(serviceMeta.getServicePort())
                .payload(serviceMeta)
                .build();
        serviceDiscovery.unregisterService(serviceInstance);
    }
    
    @Override
    public ServiceMeta discovery(String serviceName, int invokeHashCode) throws Exception {
        Collection<ServiceInstance<ServiceMeta>> services = serviceDiscovery.queryForInstances(serviceName);
        ZKConsistentHashLoadBalancer loadBalancer = new ZKConsistentHashLoadBalancer();
        ServiceInstance<ServiceMeta> instance = loadBalancer.select((List<ServiceInstance<ServiceMeta>>) services, invokeHashCode);
        if (instance == null)
            throw new FireflyException(ExceptionType.SERVICE_NOT_FOUND);
        return instance.getPayload();
    }
    
    @Override
    public void destroy() throws IOException {
        this.serviceDiscovery.close();
    }
}
