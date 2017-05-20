package io.github.jistol.remote.configuration;

import io.github.jistol.remote.Protocol;
import io.github.jistol.remote.annotation.HttpInvokerServer;
import io.github.jistol.remote.annotation.RmiServer;
import io.github.jistol.remote.model.RemoteServer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;

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
        if (bean.getClass().isAnnotationPresent(RmiServer.class)) {
            RmiServer rmiServer = AnnotationUtils.findAnnotation(bean.getClass(), RmiServer.class);
            return Protocol.RMI.getServiceExporter(bean, beanName, new RemoteServer(rmiServer), applicationContext.getEnvironment());
        } else if (bean.getClass().isAnnotationPresent(HttpInvokerServer.class)) {
            HttpInvokerServer httpInvokerServer = AnnotationUtils.findAnnotation(bean.getClass(), HttpInvokerServer.class);
            return Protocol.HTTP.getServiceExporter(bean, beanName, new RemoteServer(httpInvokerServer), applicationContext.getEnvironment());
        }

        return bean;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
