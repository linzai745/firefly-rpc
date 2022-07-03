package com.firefly.rpc.client.invoker;

import com.firefly.rpc.client.FireflyInvokerProxy;
import firefly.rpc.registry.RegistryFactory;
import firefly.rpc.registry.RegistryService;
import firefly.rpc.registry.RegistryType;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

public class FireflyFactoryBean implements FactoryBean<Object> {
    private Object object;
    private Class<?> interfaceClass;
    private String serviceVersion;
    private String registryType;
    private String registryAddr;
    private long timeout;
    
    @Override
    public Object getObject() throws Exception {
        return object;
    }
    
    public void init() throws Exception {
        RegistryService registryService = RegistryFactory.getInstance(this.registryAddr, RegistryType.valueOf(this.registryType));
        this.object = Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new FireflyInvokerProxy(serviceVersion, timeout, registryService));
    }
    
    @Override
    public Class<?> getObjectType() {
        return interfaceClass;
    }
    
    public void setInterfaceClass(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }
    
    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }
    
    public void setRegistryType(String registryType) {
        this.registryType = registryType;
    }
    
    public void setRegistryAddr(String registryAddr) {
        this.registryAddr = registryAddr;
    }
    
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
}
