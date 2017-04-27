package io.github.jistol.remote;

import org.springframework.beans.FatalBeanException;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;
import org.springframework.remoting.rmi.RmiServiceExporter;

import java.rmi.RemoteException;

/**
 * Created by kimjh on 2017-03-07.
 */
public enum Protocol
{
    HTTP {
        @Override
        public Object getServiceExporter(Object bean, String beanName, RemoteServer remoteServer) {
            HttpInvokerServiceExporter httpInvokerServiceExporter = new HttpInvokerServiceExporter();
            httpInvokerServiceExporter.setServiceInterface(remoteServer.serviceInterface());
            httpInvokerServiceExporter.setService(bean);
            httpInvokerServiceExporter.afterPropertiesSet();
            return httpInvokerServiceExporter;
        }
    },

    RMI {
        @Override
        public Object getServiceExporter(Object bean, String beanName, RemoteServer remoteServer) {
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
    };

    abstract public Object getServiceExporter(Object bean, String beanName, RemoteServer remoteServer);
}
