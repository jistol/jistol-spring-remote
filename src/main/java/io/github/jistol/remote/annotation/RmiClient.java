package io.github.jistol.remote.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by kimjh on 2017-03-07.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface RmiClient
{
    String ip();

    String port() default "";

    boolean lookupStubOnStartup() default true;

    boolean cacheStub() default  true;

    boolean refreshStubOnConnectFailure() default false;
}
