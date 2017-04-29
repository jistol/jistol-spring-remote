package io.github.jistol.remote.test.server.impl;

import io.github.jistol.remote.Protocol;
import io.github.jistol.remote.annotation.RemoteServer;
import io.github.jistol.remote.test.server.HelloService;
import org.springframework.stereotype.Service;

/**
 * Created by kimjh on 2017-04-29.
 */
@Service("helloService")
@RemoteServer(protocol = Protocol.RMI, serviceInterface = HelloService.class)
public class HelloServiceImpl implements HelloService {
    @Override
    public String say() {
        Class clazz = this.getClass();
        return clazz.getName() +  "." + clazz.getEnclosingMethod().getName();
    }
}
