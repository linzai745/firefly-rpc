package firefly.rpc.registry;

public class RegistryFactory {
    private static volatile RegistryService registryService;
    
    public static RegistryService getInstance(String registryAddr, RegistryType type) throws Exception {
        if (null == registryService) {
            synchronized (RegistryService.class) {
                if (null == registryService) {
                    if (type == RegistryType.ZOOKEEPER) {
                        registryService = new ZookeeperRegistryService(registryAddr);
                    }
                    if (type == RegistryType.EUREKA) {
                        registryService = null;
                    }
                }
            }
        }
        return registryService;
    }
}
