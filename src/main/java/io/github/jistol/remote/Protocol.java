package io.github.jistol.remote;

import io.github.jistol.remote.model.RemoteClient;
import io.github.jistol.remote.model.RemoteServer;
import io.github.jistol.remote.util.FactoryBeanUtil;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.env.Environment;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;
import org.springframework.remoting.rmi.RmiServiceExporter;

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
            httpInvokerServiceExporter.setServiceInterface(remoteServer.getServiceInterface());
            httpInvokerServiceExporter.setService(bean);
            httpInvokerServiceExporter.afterPropertiesSet();
            return httpInvokerServiceExporter;
        }

        @Override public FactoryBean getProxyFactoryBean(RemoteClient remoteClient, String context, Class returnType, Environment environment) {
            HttpInvokerProxyFactoryBean factoryBean = FactoryBeanUtil.getHttpInvokerProxyFactoryBean(getUrl(environment, remoteClient, context, "http", "80"), returnType);
            return factoryBean;
        }
    },

    RMI {
        @Override public Object getServiceExporter(Object bean, String beanName, RemoteServer remoteServer, Environment environment) {
            RmiServiceExporter rmiServiceExporter = new RmiServiceExporter();
            rmiServiceExporter.setServiceInterface(remoteServer.getServiceInterface());
            rmiServiceExporter.setService(bean);
            rmiServiceExporter.setServiceName(beanName);
            executeif(()-> !isEmpty(remoteServer.getHost()), ()-> rmiServiceExporter.setRegistryHost(remoteServer.getHost()));
            executeif(()-> !isEmpty(remoteServer.getPort()), ()-> rmiServiceExporter.setRegistryPort(getPort(environment, remoteServer.getPort(), "1099")));

            try {
                rmiServiceExporter.afterPropertiesSet();
            } catch (RemoteException e) {
                throw new FatalBeanException("Exception initializing RmiServiceExporter", e);
            }
            return rmiServiceExporter;
        }

        @Override public FactoryBean getProxyFactoryBean(RemoteClient remoteClient, String context, Class returnType, Environment environment) {
            RmiProxyFactoryBean factoryBean = FactoryBeanUtil.getRmiProxyFactoryBean(getUrl(environment, remoteClient, context, "rmi", "443"), returnType, remoteClient.isCacheStub(), remoteClient.isLookupStubOnStartup(), remoteClient.isRefreshStubOnConnectFailure());
            return factoryBean;
        }
    };


    int getPort(Environment environment, String port, String defaultStr) {
        return Integer.parseInt(isEmpty(port)? defaultStr : replace(environment, port));
    }

    String getUrl(Environment environment, RemoteClient client, String urlContext, String protocol, String defaultPort) {
        String port = isEmpty(client.getPort())? defaultPort : replace(environment, client.getPort());
        String url = protocol + "://" + replace(environment, client.getIp()) + ":" + port + "/" + urlContext;
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

    public static boolean isEmpty(String str) { return str == null || str.isEmpty(); }

    public static void executeif(Supplier<Boolean> condition, VoidSupplier executor) {
        if (condition.get()) { executor.execute(); }
    }

    private static final Pattern pattern = Pattern.compile("\\$\\{(\\S+)\\}");


    abstract public Object getServiceExporter(Object bean, String beanName, RemoteServer remoteServer, Environment environment);
    abstract public FactoryBean getProxyFactoryBean(RemoteClient remoteClient, String context, Class returnType, Environment environment);

    private interface VoidSupplier { void execute(); }
}
