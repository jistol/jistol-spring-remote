package io.github.jistol.remote;

import io.github.jistol.remote.annotation.RemoteServer;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;
import org.springframework.remoting.rmi.RmiServiceExporter;

import java.rmi.RemoteException;

/**
 * Created by kimjh on 2017-03-07.
 */
public enum Protocol
{
    HTTP {
        @Override public Object getServiceExporter(Object bean, String beanName, RemoteServer remoteServer) {
            HttpInvokerServiceExporter httpInvokerServiceExporter = new HttpInvokerServiceExporter();
            httpInvokerServiceExporter.setServiceInterface(remoteServer.serviceInterface());
            httpInvokerServiceExporter.setService(bean);
            httpInvokerServiceExporter.afterPropertiesSet();
            return httpInvokerServiceExporter;
        }

        @Override public FactoryBean getProxyFactoryBean(String url, Class returnType) {
            HttpInvokerProxyFactoryBean httpInvokerProxyFactoryBean = new HttpInvokerProxyFactoryBean();
            httpInvokerProxyFactoryBean.setServiceUrl(url);
            httpInvokerProxyFactoryBean.setServiceInterface(returnType);
            httpInvokerProxyFactoryBean.afterPropertiesSet();
            return httpInvokerProxyFactoryBean;
        }

        @Override
        public String getProtocolName() {
            return "http";
        }

        @Override
        public String getDefaultPort() {
            return "80";
        }
    },

    RMI {
        @Override public Object getServiceExporter(Object bean, String beanName, RemoteServer remoteServer) {
            RmiServiceExporter rmiServiceExporter = new RmiServiceExporter();
            rmiServiceExporter.setServiceInterface(remoteServer.serviceInterface());
            rmiServiceExporter.setService(bean);
            rmiServiceExporter.setServiceName(beanName);
            if (remoteServer.port() != -1)
            {
                rmiServiceExporter.setRegistryPort(remoteServer.port());
            }
            try
            {
                rmiServiceExporter.afterPropertiesSet();
            }
            catch (RemoteException e)
            {
                throw new FatalBeanException("Exception initializing RmiServiceExporter", e);
            }
            return rmiServiceExporter;
        }

        @Override public FactoryBean getProxyFactoryBean(String url, Class returnType) {
            RmiProxyFactoryBean rmiProxyFactoryBean = new RmiProxyFactoryBean();
            rmiProxyFactoryBean.setServiceUrl(url);
            rmiProxyFactoryBean.setServiceInterface(returnType);
            rmiProxyFactoryBean.afterPropertiesSet();
            return rmiProxyFactoryBean;
        }

        @Override
        public String getProtocolName() {
            return "rmi";
        }

        @Override
        public String getDefaultPort() {
            return "443";
        }
    };

    abstract public Object getServiceExporter(Object bean, String beanName, RemoteServer remoteServer);
    abstract public FactoryBean getProxyFactoryBean(String url, Class returnType);
    abstract public String getProtocolName();
    abstract public String getDefaultPort();
}
