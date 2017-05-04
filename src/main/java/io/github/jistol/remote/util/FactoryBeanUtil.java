package io.github.jistol.remote.util;

import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;

/**
 * Created by kimjh on 2017-05-04.
 */
public class FactoryBeanUtil {
    public static HttpInvokerProxyFactoryBean getHttpInvokerProxyFactoryBean(String url, Class<?> serviceInterface) {
        return getHttpInvokerProxyFactoryBean(url, serviceInterface, true);
    }

    public static HttpInvokerProxyFactoryBean getHttpInvokerProxyFactoryBean(String url, Class<?> serviceInterface, boolean doInit) {
        HttpInvokerProxyFactoryBean httpInvokerProxyFactoryBean = new HttpInvokerProxyFactoryBean();
        httpInvokerProxyFactoryBean.setServiceUrl(url);
        httpInvokerProxyFactoryBean.setServiceInterface(serviceInterface);
        if (doInit) httpInvokerProxyFactoryBean.afterPropertiesSet();
        return httpInvokerProxyFactoryBean;
    }

    public static RmiProxyFactoryBean getRmiProxyFactoryBean(String url, Class<?> serviceInterface) {
        return getRmiProxyFactoryBean(url, serviceInterface, true, true, false, true);
    }

    public static RmiProxyFactoryBean getRmiProxyFactoryBean(String url, Class<?> serviceInterface, boolean doInit) {
        return getRmiProxyFactoryBean(url, serviceInterface, true, true, false, doInit);
    }

    public static RmiProxyFactoryBean getRmiProxyFactoryBean(String url, Class<?> serviceInterface, boolean cacheStub, boolean lookupStubOnStartup, boolean refreshStubOnConnectFailure) {
        return getRmiProxyFactoryBean(url, serviceInterface, cacheStub, lookupStubOnStartup, refreshStubOnConnectFailure, true);
    }

    public static RmiProxyFactoryBean getRmiProxyFactoryBean(String url, Class<?> serviceInterface, boolean cacheStub, boolean lookupStubOnStartup, boolean refreshStubOnConnectFailure, boolean doInit) {
        RmiProxyFactoryBean rmiProxyFactoryBean = new RmiProxyFactoryBean();
        rmiProxyFactoryBean.setServiceUrl(url);
        rmiProxyFactoryBean.setServiceInterface(serviceInterface);
        rmiProxyFactoryBean.setCacheStub(cacheStub);
        rmiProxyFactoryBean.setLookupStubOnStartup(lookupStubOnStartup);
        rmiProxyFactoryBean.setRefreshStubOnConnectFailure(refreshStubOnConnectFailure);
        if(doInit) rmiProxyFactoryBean.afterPropertiesSet();
        return rmiProxyFactoryBean;
    }
}
