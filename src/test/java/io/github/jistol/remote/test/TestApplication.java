package io.github.jistol.remote.test;

import io.github.jistol.remote.annotation.EnableRemoteClient;
import io.github.jistol.remote.annotation.EnableRemoteServer;
import io.github.jistol.remote.test.client.RmiServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by kimjh on 2017-04-29.
 */
@SpringBootApplication
@EnableRemoteClient(basePackage = "io.github.jistol.remote.test")
@EnableRemoteServer
@RestController
public class TestApplication {

    public static void main(String... args) {
        SpringApplication.run(TestApplication.class, args);
    }

    @Autowired RmiServiceClient rmiServiceClient;

    @RequestMapping("/")
    public String say()
    {
        return rmiServiceClient.helloService().say();
    }
}
