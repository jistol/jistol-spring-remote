package io.github.jistol.remote;

import io.github.jistol.remote.annotation.RemoteClient;
import io.github.jistol.remote.annotation.RemoteServer;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.env.Environment;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;
import org.springframework.remoting.rmi.RmiServiceExporter;
import org.springframework.util.StringUtils;

import java.rmi.RemoteException;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kimjh on 2017-03-07.
 */
public enum Protocol
{
    HTTP {
        @Override public Object getServiceExporter(Object bean, String beanName, RemoteServer remoteServer, Environment environment) {
            HttpInvokerServiceExporter httpInvokerServiceExporter = new HttpInvokerServiceExporter();
            httpInvokerServiceExporter.setServiceInterface(remoteServer.serviceInterface());
            httpInvokerServiceExporter.setService(bean);
            httpInvokerServiceExporter.afterPropertiesSet();
            return httpInvokerServiceExporter;
        }

        @Override public FactoryBean getProxyFactoryBean(RemoteClient remoteClient, String context, Class returnType, Environment environment) {
            HttpInvokerProxyFactoryBean httpInvokerProxyFactoryBean = new HttpInvokerProxyFactoryBean();
            httpInvokerProxyFactoryBean.setServiceUrl(getUrl(environment, remoteClient, context, "80"));
            httpInvokerProxyFactoryBean.setServiceInterface(returnType);
            httpInvokerProxyFactoryBean.afterPropertiesSet();
            return httpInvokerProxyFactoryBean;
        }

        @Override public String getProtocolName() {
            return "http";
        }
    },

    RMI {
        @Override public Object getServiceExporter(Object bean, String beanName, RemoteServer remoteServer, Environment environment) {
            RmiServiceExporter rmiServiceExporter = new RmiServiceExporter();
            rmiServiceExporter.setServiceInterface(remoteServer.serviceInterface());
            rmiServiceExporter.setService(bean);
            rmiServiceExporter.setServiceName(beanName);
            executeif(()-> !StringUtils.isEmpty(remoteServer.host()), ()-> rmiServiceExporter.setRegistryHost(remoteServer.host()));
            executeif(()-> !StringUtils.isEmpty(remoteServer.port()), ()-> rmiServiceExporter.setRegistryPort(getPort(environment, remoteServer.port(), "1099")));

            try {
                rmiServiceExporter.afterPropertiesSet();
            } catch (RemoteException e) {
                throw new FatalBeanException("Exception initializing RmiServiceExporter", e);
            }
            return rmiServiceExporter;
        }

        @Override public FactoryBean getProxyFactoryBean(RemoteClient remoteClient, String context, Class returnType, Environment environment) {
            RmiProxyFactoryBean rmiProxyFactoryBean = new RmiProxyFactoryBean();
            rmiProxyFactoryBean.setServiceUrl(getUrl(environment, remoteClient, context, "443"));
            rmiProxyFactoryBean.setServiceInterface(returnType);
            rmiProxyFactoryBean.afterPropertiesSet();
            return rmiProxyFactoryBean;
        }

        @Override public String getProtocolName() {
            return "rmi";
        }
    };

    void executeif(Supplier<Boolean> condition, VoidSupplier executor) {
        if (condition.get()) { executor.execute(); }
    }

    int getPort(Environment environment, String port, String defaultStr) {
        return Integer.parseInt(isEmpty(port)? defaultStr : replace(environment, port));
    }

    String getUrl(Environment environment, RemoteClient client, String urlContext, String defaultPort) {
        String port = isEmpty(client.port())? defaultPort : replace(environment, client.port());
        String url = this.getProtocolName() + "://" + replace(environment, client.ip()) + ":" + port + "/" + urlContext;
        return url;
    }

    String replace(Environment environment, String target) {
        StringBuffer result = new StringBuffer();
        Matcher matcher = pattern.matcher(target);
        while (matcher.find()) {
            String[] prop = matcher.group(1).split(":");
            matcher.appendReplacement(result, environment.getProperty(prop[0], prop.length > 1? prop[1] : ""));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    boolean isEmpty(String str) { return str == null || str.isEmpty(); }

    private static final Pattern pattern = Pattern.compile("\\$\\{(\\S+)\\}");

    abstract public Object getServiceExporter(Object bean, String beanName, RemoteServer remoteServer, Environment environment);
    abstract public FactoryBean getProxyFactoryBean(RemoteClient remoteClient, String context, Class returnType, Environment environment);
    abstract public String getProtocolName();

    private interface VoidSupplier { void execute(); }
}
