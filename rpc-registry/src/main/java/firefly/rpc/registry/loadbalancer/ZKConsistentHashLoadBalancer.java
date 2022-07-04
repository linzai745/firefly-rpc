package firefly.rpc.registry.loadbalancer;

import firefly.rpc.core.ServiceMeta;
import org.apache.curator.x.discovery.ServiceInstance;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ZKConsistentHashLoadBalancer implements ServiceLoadBalancer<ServiceInstance<ServiceMeta>> {
    private static final int VIRTUAL_NODE_SIZE = 10;
    
    private static final String VIRTUAL_NODE_SPLIT = "#";
    
    @Override
    public ServiceInstance<ServiceMeta> select(List<ServiceInstance<ServiceMeta>> servers, int hashCode) {
        if (servers.isEmpty()) return null;
        TreeMap<Integer, ServiceInstance<ServiceMeta>> ring = makeConsistentHashRing(servers);
        return allocateNode(ring, hashCode);
    }
    
    private TreeMap<Integer, ServiceInstance<ServiceMeta>> makeConsistentHashRing(List<ServiceInstance<ServiceMeta>> servers) {
        TreeMap<Integer, ServiceInstance<ServiceMeta>> ring = new TreeMap<>();
        for (ServiceInstance<ServiceMeta> instance : servers) {
            joinVirtualNode(instance, ring);
        }
        return ring;
    }
    
    private void joinVirtualNode(ServiceInstance<ServiceMeta> instance, TreeMap<Integer, ServiceInstance<ServiceMeta>> ring) {
        for (int i = 0; i < VIRTUAL_NODE_SIZE; i++) {
            int hashCode = buildServiceInstanceKey(instance, i);
            ring.put(hashCode, instance);
        }
    }
    
    private int buildServiceInstanceKey(ServiceInstance<ServiceMeta> instance, int num) {
        ServiceMeta payload = instance.getPayload();
        String address = String.join(":", payload.getServiceAddr(), String.valueOf(payload.getServicePort()));
        String key = String.join(VIRTUAL_NODE_SPLIT, address, String.valueOf(num));
        return key.hashCode();
    }
    
    /**
     * if hash ring have not ceiling target node, return first entry instance, ignore that service correct
     * @param ring
     * @param hashCode
     * @return
     */
    private ServiceInstance<ServiceMeta> allocateNode(TreeMap<Integer, ServiceInstance<ServiceMeta>> ring, int hashCode) {
        Map.Entry<Integer, ServiceInstance<ServiceMeta>> serviceEntry = ring.ceilingEntry(hashCode);
        if (serviceEntry == null) {
            Map.Entry<Integer, ServiceInstance<ServiceMeta>> firstEntry = ring.firstEntry();
            return firstEntry.getValue();
        }
        return serviceEntry.getValue();
    }
}
