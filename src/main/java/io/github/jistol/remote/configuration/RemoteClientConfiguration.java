package io.github.jistol.remote.configuration;

import io.github.jistol.remote.Protocol;
import io.github.jistol.remote.annotation.RemoteClient;
import io.github.jistol.remote.annotation.RemoteContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by kimjh on 2017-03-07.
 */
@Configuration
public class RemoteClientConfiguration implements BeanPostProcessor, InvocationHandler
{
    private static final ConcurrentMap<String, FactoryBean> rmiClientMap = new ConcurrentHashMap<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException
    {
        if (!bean.getClass().isInterface() || !bean.getClass().isAnnotationPresent(RemoteClient.class)) return bean;
        Class<?> serviceInterface = bean.getClass();
        return serviceInterface.cast(Proxy.newProxyInstance(serviceInterface.getClassLoader(), new Class[]{ serviceInterface }, this));
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException
    {
        return bean;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            return getFactoryBean(proxy, method).getObject();
        } catch (Exception e) {
            // 재연결을 위해 한번더 시도한다.
            rmiClientMap.remove(getKey(proxy, method));
            return getFactoryBean(proxy, method).getObject();
        }
    }

    private String getKey(Object proxy, Method method) {
        return proxy.getClass().getName() + method.getName();
    }

    private FactoryBean getFactoryBean(Object proxy, Method method) {
        Class<?> returnType = method.getReturnType();
        return rmiClientMap.computeIfAbsent(getKey(proxy, method), name -> {
            RemoteClient client = proxy.getClass().getAnnotation(RemoteClient.class);
            RemoteContext context = method.getAnnotation(RemoteContext.class);
            Protocol protocol = client.protocol();
            String urlContext = context != null ? context.context() : method.getName();
            String port = StringUtils.isEmpty(client.port())? protocol.getDefaultPort() : client.port();
            String url = protocol.getProtocolName() + "//" + client.ip() + ":" + port + "/" + urlContext;
            return protocol.getProxyFactoryBean(url, returnType);
        });
    }
}
