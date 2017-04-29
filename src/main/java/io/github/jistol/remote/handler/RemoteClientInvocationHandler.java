package io.github.jistol.remote.handler;

import io.github.jistol.remote.Protocol;
import io.github.jistol.remote.annotation.RemoteClient;
import io.github.jistol.remote.annotation.RemoteContext;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by kimjh on 2017-04-30.
 */
public class RemoteClientInvocationHandler implements InvocationHandler {
    private final ConcurrentMap<String, FactoryBean> rmiClientMap = new ConcurrentHashMap<>();
    private final RemoteClient client;

    public RemoteClientInvocationHandler(RemoteClient remoteClient)
    {
        this.client = remoteClient;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> returnType = method.getReturnType();
        if (!returnType.isAssignableFrom(Void.class))
        {
            try {
                return returnType.cast(getFactoryBean(method).getObject());
            } catch (Exception e) {
                // 재연결을 위해 한번더 시도한다.
                rmiClientMap.remove(getKey(method));
                return returnType.cast(getFactoryBean(method).getObject());
            }
        }
        return null;
    }

    private String getKey(Method method) {
        return method.getName();
    }

    private FactoryBean getFactoryBean(Method method) {
        Class<?> returnType = method.getReturnType();
        return rmiClientMap.computeIfAbsent(getKey(method), name -> {
            RemoteContext context = method.getAnnotation(RemoteContext.class);
            Protocol protocol = client.protocol();
            String urlContext = context != null ? context.context() : method.getName();
            String port = StringUtils.isEmpty(client.port())? protocol.getDefaultPort() : client.port();
            String url = protocol.getProtocolName() + "//" + client.ip() + ":" + port + "/" + urlContext;
            return protocol.getProxyFactoryBean(url, returnType);
        });
    }
}
