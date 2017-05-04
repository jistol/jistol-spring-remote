package io.github.jistol.remote.test.client;

import io.github.jistol.remote.annotation.HttpInvokerClient;
import io.github.jistol.remote.annotation.RemoteContext;
import io.github.jistol.remote.test.server.HttpInvokerService;

/**
 * Created by kimjh on 2017-05-04.
 */
@HttpInvokerClient(ip = "${test.http.server.ip}", port = "${test.http.server.port}")
public interface HttpServiceClient {
    @RemoteContext(context = "httpInvokerService")
    HttpInvokerService httpInvokerService();
}
