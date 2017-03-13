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
        public Object getServiceExporter(Object bean, String beanName, RemoteType remoteType) {
            HttpInvokerServiceExporter httpInvokerServiceExporter = new HttpInvokerServiceExporter();
            httpInvokerServiceExporter.setServiceInterface(remoteType.serviceInterface());
            httpInvokerServiceExporter.setService(bean);
            httpInvokerServiceExporter.afterPropertiesSet();
            return httpInvokerServiceExporter;
        }
    },

    RMI {
        @Override
        public Object getServiceExporter(Object bean, String beanName, RemoteType remoteType) {
            RmiServiceExporter rmiServiceExporter = new RmiServiceExporter();
            rmiServiceExporter.setServiceInterface(remoteType.serviceInterface());
            rmiServiceExporter.setService(bean);
            rmiServiceExporter.setServiceName(beanName);
            if (remoteType.port() != -1)
            {
                rmiServiceExporter.setRegistryPort(remoteType.port());
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

    abstract public Object getServiceExporter(Object bean, String beanName, RemoteType remoteType);
}
