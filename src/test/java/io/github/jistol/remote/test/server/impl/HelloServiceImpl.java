package io.github.jistol.remote.test.server.impl;

import io.github.jistol.remote.Protocol;
import io.github.jistol.remote.annotation.RemoteServer;
import io.github.jistol.remote.test.server.HelloService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by kimjh on 2017-04-29.
 */
@Service("helloService")
@RemoteServer(protocol = Protocol.RMI, serviceInterface = HelloService.class, port = "${test.rmi.port}")
public class HelloServiceImpl implements HelloService {
    @Value("${test.rmi.port}") private String rmiPort;
    @Override
    public String say() {
        Class clazz = this.getClass();
        return clazz.getName() +  "." + new Object(){}.getClass().getEnclosingMethod().getName() + ", rmiPort :" + rmiPort;
    }
}
