package io.github.jistol.remote.model;

import io.github.jistol.remote.Protocol;
import io.github.jistol.remote.annotation.HttpInvokerServer;
import io.github.jistol.remote.annotation.RmiServer;

/**
 * Created by kimjh on 2017-05-04.
 */
public class RemoteServer {
    private String host;
    private String port;
    private Protocol protocol;
    private Class<?> serviceInterface;

    public RemoteServer(RmiServer rmiServer) {
        setHost(rmiServer.host());
        setPort(rmiServer.port());
        setServiceInterface(rmiServer.serviceInterface());
        setProtocol(Protocol.RMI);
    }

    public RemoteServer(HttpInvokerServer httpInvokerServer) {
        setServiceInterface(httpInvokerServer.serviceInterface());
        setProtocol(Protocol.HTTP);
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public Class<?> getServiceInterface() {
        return serviceInterface;
    }

    public void setServiceInterface(Class<?> serviceInterface) {
        this.serviceInterface = serviceInterface;
    }
}
