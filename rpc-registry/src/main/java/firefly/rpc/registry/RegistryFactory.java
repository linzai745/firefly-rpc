package firefly.rpc.registry;

public class RegistryFactory {
    private static volatile RegistryService registryService;
    
    public static RegistryService getInstance(String registryAddr, RegistryType type) {
        return registryService;
    }
}
