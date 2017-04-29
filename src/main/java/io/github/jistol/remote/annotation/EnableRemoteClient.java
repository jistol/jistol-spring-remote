package io.github.jistol.remote.annotation;

import io.github.jistol.remote.configuration.RemoteClientConfiguration;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by kimjh on 2017-03-13.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Import(RemoteClientConfiguration.class)
public @interface EnableRemoteClient
{
    @Required String basePackage();
}
