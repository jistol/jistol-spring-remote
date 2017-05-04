package io.github.jistol.remote.test.server.impl;

import io.github.jistol.remote.annotation.RmiServer;
import io.github.jistol.remote.test.server.RmiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by kimjh on 2017-04-29.
 */
@Service("rmiService")
@RmiServer(serviceInterface = RmiService.class, port = "${test.rmi.port}")
public class RmiServiceImpl implements RmiService {
    @Value("${test.rmi.port}") private String rmiPort;

    @Override
    public String say() {
        Class clazz = this.getClass();
        return clazz.getName() +  "." + new Object(){}.getClass().getEnclosingMethod().getName() + ", rmiPort :" + rmiPort;
    }
}
