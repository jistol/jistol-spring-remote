package io.github.jistol.remote.configuration;

import io.github.jistol.remote.annotation.RemoteServer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;

/**
 * Created by kimjh on 2017-03-07.
 */
@Configuration
public class RemoteServerConfiguration implements BeanPostProcessor, ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException
    {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException
    {
        RemoteServer remoteServer = AnnotationUtils.findAnnotation(bean.getClass(), RemoteServer.class);
        return (remoteServer == null)? bean : remoteServer.protocol().getServiceExporter(bean, beanName, remoteServer, applicationContext.getEnvironment());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
