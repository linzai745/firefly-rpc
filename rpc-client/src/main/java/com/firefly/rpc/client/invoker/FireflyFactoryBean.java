package com.firefly.rpc.client.invoker;

import org.springframework.beans.factory.FactoryBean;

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
        // TODO 生成client bean的动态代理对象
    }
    
    @Override
    public Class<?> getObjectType() {
        return interfaceClass;
    }
}
