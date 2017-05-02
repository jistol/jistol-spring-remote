package io.github.jistol.remote.test.client;

import io.github.jistol.remote.Protocol;
import io.github.jistol.remote.annotation.RemoteClient;
import io.github.jistol.remote.test.server.HelloService;

/**
 * Created by kimjh on 2017-04-29.
 */
@RemoteClient(protocol = Protocol.RMI, ip = "${test.rmi.server.ip}", port = "${test.rmi.server.port}")
public interface RmiServiceClient {
    //@RemoteContext(context = "HelloService")
    HelloService helloService();
}
