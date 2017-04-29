package io.github.jistol.remote.configuration;

import io.github.jistol.remote.annotation.EnableRemoteClient;
import io.github.jistol.remote.annotation.RemoteClient;
import io.github.jistol.remote.exception.RemoteException;
import io.github.jistol.remote.handler.RemoteClientInvocationHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
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
public class RemoteClientConfiguration implements ImportAware, BeanFactoryPostProcessor
{
    private static final ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();

    private Stream<Class<?>> findAnnotatedClasses(Class<? extends Annotation> annotation, String scanPackage) {
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false) {
            @Override protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                System.out.println("### isCandidateComponent : " +  beanDefinition.toString());
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
        while (!queue.isEmpty())
        {
            String basePackage = queue.poll();
            findAnnotatedClasses(RemoteClient.class, basePackage).forEach(clazz -> {
                RemoteClient remoteClient = clazz.getAnnotation(RemoteClient.class);
                String beanName = clazz.getName();
                System.out.println("#### postProcessBeanFactory bean name : " + beanName);
                Object bean = clazz.cast(Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{ clazz }, new RemoteClientInvocationHandler(remoteClient)));
                System.out.println("#### postProcessBeanFactory bean class.isInstance : " + (clazz.isInstance(bean)));
                beanFactory.registerSingleton(beanName, bean);
            });
        }
    }

    @Override
    public void setImportMetadata(AnnotationMetadata annotationMetadata) {
        Map<String, Object> attrMap = annotationMetadata.getAnnotationAttributes(EnableRemoteClient.class.getName(), false);
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(attrMap);
        String basePackage = attributes.getString("basePackage");
        queue.add(basePackage);
        System.out.println("### basePackage : " + basePackage);
    }
}
