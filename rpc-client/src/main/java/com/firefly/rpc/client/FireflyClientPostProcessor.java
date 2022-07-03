package com.firefly.rpc.client;

import com.firefly.rpc.client.annotation.FireflyClient;
import com.firefly.rpc.client.invoker.FireflyFactoryBean;
import firefly.rpc.core.FireflyConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 使用ApplicationContextAware 接口获取Spring Ioc 容器
 * 使用 BeanClassLoaderAware 接口注入Bean的 ClassLoader
 * 使用 BeanFactoryPostProcessor 自定义FireflyFactoryBean 的Bean属性
 */

@Component
@Slf4j
public class FireflyClientPostProcessor implements ApplicationContextAware, BeanClassLoaderAware, BeanFactoryPostProcessor {
    private ClassLoader classLoader;
    private ApplicationContext context;
    private final Map<String, BeanDefinition> rpcRefBeanDefinitions = new HashMap<>();
    
    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
    
    /**
     * 创建@FireflyClient 注解的Bean对象
     * @param beanFactory
     * @throws BeansException
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        String[] beanNames = beanFactory.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
            String className = beanDefinition.getBeanClassName();
            buildBeanDefinition(className);
        }
    
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
        this.rpcRefBeanDefinitions.forEach((beanName, beanDefinition) -> {
            if (context.containsBean(beanName))
                throw new IllegalArgumentException("Spring context already has a bean named " + beanName);
            registry.registerBeanDefinition(beanName, beanDefinition);
            log.info("registered FireflyReferenceBean {} success.", beanName);
        });
    }
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
    
    private void buildBeanDefinition(String className) {
        if (className == null) return;
        Class<?> clazz = ClassUtils.resolveClassName(className, this.classLoader);
        ReflectionUtils.doWithFields(clazz, this::parseRpcReference);
    }
    
    private void parseRpcReference(Field field) {
        FireflyClient annotation = AnnotationUtils.getAnnotation(field, FireflyClient.class);
        if (annotation == null) return;
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(FireflyFactoryBean.class)
                .setInitMethodName(FireflyConstants.INIT_METHOD_NAME)
                .addPropertyValue(FireflyConstants.REFERENCE_INTERFACE_FIELD, field.getType())
                .addPropertyValue(FireflyConstants.REFERENCE_SERVICE_VERSION_FIELD, annotation.serviceVersion())
                .addPropertyValue(FireflyConstants.REFERENCE_REGISTRY_TYPE_FIELD, annotation.registryType())
                .addPropertyValue(FireflyConstants.REFERENCE_REGISTRY_ADDRESS_FIELD, annotation.registryAddress())
                .addPropertyValue(FireflyConstants.REFERENCE_TIMEOUT_FIELD, annotation.timeout());
    
        AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
        rpcRefBeanDefinitions.put(field.getName(), beanDefinition);
    }
}
