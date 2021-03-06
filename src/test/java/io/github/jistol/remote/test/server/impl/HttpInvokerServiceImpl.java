package io.github.jistol.remote.test.server.impl;

import io.github.jistol.remote.annotation.HttpInvokerServer;
import io.github.jistol.remote.test.server.HttpInvokerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by kimjh on 2017-05-04.
 */
@Service("/httpInvokerService")
@HttpInvokerServer(serviceInterface = HttpInvokerService.class)
public class HttpInvokerServiceImpl implements HttpInvokerService {
    @Value("${server.port}") private String testHttpPort;

    @Override public String say() {
        Class clazz = this.getClass();
        return clazz.getName() +  "." + new Object(){}.getClass().getEnclosingMethod().getName() + ", httpPort :" + testHttpPort;
    }
}
