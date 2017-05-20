package io.github.jistol.remote.model;

import io.github.jistol.remote.Protocol;
import io.github.jistol.remote.annotation.HttpInvokerClient;
import io.github.jistol.remote.annotation.RmiClient;

import java.lang.annotation.Annotation;

/**
 * Created by kimjh on 2017-05-04.
 */
public class RemoteClient {
    private Protocol protocol;
    private String ip;
    private String port;
    private boolean lookupStubOnStartup;
    private boolean cacheStub;
    private boolean refreshStubOnConnectFailure;

    public <T extends Annotation> RemoteClient(T client)
    {
        Initializer.valueOf(client.annotationType().getSimpleName()).init(this, client);
    }

    private enum Initializer
    {
        HttpInvokerClient {
            @Override public void init(RemoteClient remoteClient, Object obj) {
                HttpInvokerClient client = HttpInvokerClient.class.cast(obj);
                remoteClient.setIp(client.ip());
                remoteClient.setPort(client.port());
                remoteClient.setProtocol(Protocol.HTTP);
            }
        },
        RmiClient {
            @Override public void init(RemoteClient remoteClient, Object obj) {
                RmiClient client = RmiClient.class.cast(obj);
                remoteClient.setIp(client.ip());
                remoteClient.setPort(client.port());
                remoteClient.setLookupStubOnStartup(client.lookupStubOnStartup());
                remoteClient.setCacheStub(client.cacheStub());
                remoteClient.setRefreshStubOnConnectFailure(client.refreshStubOnConnectFailure());
                remoteClient.setProtocol(Protocol.RMI);
            }
        };

        abstract public void init(RemoteClient remoteClient, Object obj);
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public boolean isLookupStubOnStartup() {
        return lookupStubOnStartup;
    }

    public void setLookupStubOnStartup(boolean lookupStubOnStartup) {
        this.lookupStubOnStartup = lookupStubOnStartup;
    }

    public boolean isCacheStub() {
        return cacheStub;
    }

    public void setCacheStub(boolean cacheStub) {
        this.cacheStub = cacheStub;
    }

    public boolean isRefreshStubOnConnectFailure() {
        return refreshStubOnConnectFailure;
    }

    public void setRefreshStubOnConnectFailure(boolean refreshStubOnConnectFailure) {
        this.refreshStubOnConnectFailure = refreshStubOnConnectFailure;
    }
}
