package io.github.jistol.remote.test.server.impl;

import io.github.jistol.remote.annotation.HttpInvokerServer;
import io.github.jistol.remote.test.server.HttpInvokerService;
import io.github.jistol.remote.test.server.DirectHttpInvokerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by kimjh on 2017-05-04.
 */
@Service("/directHttpInvokerService")
@HttpInvokerServer(serviceInterface = DirectHttpInvokerService.class)
public class DirectHttpInvokerServiceImpl implements DirectHttpInvokerService {
    @Value("${server.port}") private String testHttpPort;

    @Override public String say() {
        Class clazz = this.getClass();
        return clazz.getName() +  "." + new Object(){}.getClass().getEnclosingMethod().getName() + ", httpPort :" + testHttpPort;
    }
}
