package io.github.jistol.remote;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * Created by kimjh on 2017-03-07.
 */
@Configuration
public class RemoteConfiguration implements BeanPostProcessor
{
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException
    {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException
    {
        RemoteType remoteType = AnnotationUtils.findAnnotation(bean.getClass(), RemoteType.class);
        return (remoteType == null)? bean : remoteType.protocol().getServiceExporter(bean, beanName, remoteType);
    }
}
