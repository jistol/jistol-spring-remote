# jistol-spring-remote

You can set up Spring RMI and HttpInvoker in only two steps.

Step 1. Set @EnableRemoteType on applicatio main.
----

    @SpringBootApplication
    @EnableRemoteType
    public class SampleSpringbootIntegrationApplication {
      public static void main(String[] args) {
        SpringApplication.run(SampleSpringbootIntegrationApplication.class, args);
      }
    }


Step 2. Add @RemoteType on your service(or component, resource ...)
----

    @Service("/HttpInvokeService")
    @RemoteType(protocol = Protocol.HTTP, serviceInterface = HttpInvokeService.class)
    public class HttpInvokeServiceImpl implements HttpInvokeService
    {
        public String say() { return "hello world"; }
    }

    @Service("RmiService")
    @RemoteType(protocol = Protocol.RMI, serviceInterface = RmiService.class, port = 1199)
    public class RmiServiceImpl implements RmiService
    {
        public String say() { return "hello world"; }
    }
