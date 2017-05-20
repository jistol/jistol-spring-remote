package io.github.jistol.remote.configuration;

import io.github.jistol.remote.annotation.EnableRemoteClient;
import io.github.jistol.remote.annotation.HttpInvokerClient;
import io.github.jistol.remote.annotation.RmiClient;
import io.github.jistol.remote.exception.RemoteException;
import io.github.jistol.remote.handler.RemoteClientInvocationHandler;
import io.github.jistol.remote.model.RemoteClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;

/**
 * Created by kimjh on 2017-03-07.
 */
@Configuration
public class RemoteClientConfiguration implements ImportAware, BeanFactoryPostProcessor, ApplicationContextAware
{
    private ApplicationContext applicationContext;

    private final Stream<Class<? extends Annotation>> remoteClients = Stream.of(RmiClient.class, HttpInvokerClient.class);

    private static final ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();

    private Stream<Class<?>> findAnnotatedClasses(Class<? extends Annotation> annotation, String scanPackage) {
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false) {
            @Override protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                return true;
            }
        };
        provider.addIncludeFilter(new AnnotationTypeFilter(annotation));
        provider.setResourceLoader(new PathMatchingResourcePatternResolver(this.getClass().getClassLoader()));
        return provider.findCandidateComponents(scanPackage).stream().map(beanDef -> {
            try {
                return Class.forName(beanDef.getBeanClassName());
            } catch (ClassNotFoundException e) {
                throw new RemoteException(e);
            }
        });
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        while (!queue.isEmpty()) {
            String basePackage = queue.poll();
            remoteClients.forEach(client -> {
                findAnnotatedClasses(client, basePackage).forEach(clazz -> {
                    Object bean = clazz.cast(Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{ clazz }, new RemoteClientInvocationHandler(applicationContext.getEnvironment(), new RemoteClient(clazz.getAnnotation(client)))));
                    beanFactory.registerSingleton(clazz.getName(), bean);
                });
            });
        }
    }

    @Override
    public void setImportMetadata(AnnotationMetadata annotationMetadata) {
        Map<String, Object> attrMap = annotationMetadata.getAnnotationAttributes(EnableRemoteClient.class.getName(), false);
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(attrMap);
        String basePackage = attributes.getString("basePackage");
        queue.add(basePackage);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
