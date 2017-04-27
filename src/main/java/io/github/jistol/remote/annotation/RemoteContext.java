package io.github.jistol.remote.annotation;

import io.github.jistol.remote.Protocol;
import org.springframework.beans.factory.annotation.Required;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by kimjh on 2017-03-07.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface RemoteContext
{
    @Required String context();
}
