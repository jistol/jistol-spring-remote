package io.github.jistol.remote.test;

import io.github.jistol.remote.Protocol;
import io.github.jistol.remote.annotation.EnableRemoteClient;
import io.github.jistol.remote.annotation.EnableRemoteServer;
import io.github.jistol.remote.test.client.HttpServiceClient;
import io.github.jistol.remote.test.client.RmiServiceClient;
import io.github.jistol.remote.test.server.DirectHttpInvokerService;
import io.github.jistol.remote.test.server.DirectRmiService;
import io.github.jistol.remote.util.FactoryBeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;
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

    @Autowired private RmiServiceClient rmiServiceClient;
    @Autowired private HttpServiceClient httpServiceClient;

    @Value("${test.rmi.server.ip}") private String rmiIp;
    @Value("${test.rmi.server.port}") private String rmiPort;
    @Value("${test.http.server.ip}") private String httpIp;
    @Value("${test.http.server.port}") private String httpPort;

    @RequestMapping({"/", "/rmi", "/rmi/say"})
    public String say()
    {
        return rmiServiceClient.rmiService().say();
    }

    @RequestMapping({"/http", "/http/say"})
    public String httpSay()
    {
        return httpServiceClient.httpInvokerService().say();
    }

    @RequestMapping({"/rmi/direct"})
    public String sayProtocol()
    {
        Class<DirectRmiService> type = DirectRmiService.class;
        RmiProxyFactoryBean rmiProxyFactoryBean = FactoryBeanUtil.getRmiProxyFactoryBean("rmi://" + rmiIp + ":" + rmiPort + "/directRmiService", type);
        return type.cast(rmiProxyFactoryBean.getObject()).say();
    }

    @RequestMapping({"/http/direct"})
    public String httpSayProtocol()
    {
        Class<DirectHttpInvokerService> type = DirectHttpInvokerService.class;
        HttpInvokerProxyFactoryBean httpInvokerProxyFactoryBean = FactoryBeanUtil.getHttpInvokerProxyFactoryBean("http://" + httpIp + ":" + httpPort + "/directHttpInvokerService", type);
        return type.cast(httpInvokerProxyFactoryBean.getObject()).say();
    }
}
