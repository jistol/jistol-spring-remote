package io.github.jistol.remote.test.client;

import io.github.jistol.remote.annotation.RmiClient;
import io.github.jistol.remote.test.server.RmiService;

/**
 * Created by kimjh on 2017-04-29.
 */
@RmiClient(ip = "${test.rmi.server.ip}", port = "${test.rmi.server.port}")
public interface RmiServiceClient {
    RmiService rmiService();
}
