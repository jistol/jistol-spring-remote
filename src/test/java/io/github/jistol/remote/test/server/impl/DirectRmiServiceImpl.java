package io.github.jistol.remote.test.server.impl;

import io.github.jistol.remote.annotation.RmiServer;
import io.github.jistol.remote.test.server.DirectRmiService;
import io.github.jistol.remote.test.server.RmiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by kimjh on 2017-04-29.
 */
@Service("directRmiService")
@RmiServer(serviceInterface = DirectRmiService.class, port = "${test.rmi.port}")
public class DirectRmiServiceImpl implements DirectRmiService {
    @Value("${test.rmi.port}") private String rmiPort;

    @Override
    public String say() {
        Class clazz = this.getClass();
        return clazz.getName() +  "." + new Object(){}.getClass().getEnclosingMethod().getName() + ", rmiPort :" + rmiPort;
    }
}
