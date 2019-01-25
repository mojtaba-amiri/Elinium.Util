package com.elinium.util.retrofit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by amiri on 9/16/2017.
 */


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ManagerFor {
    Class<? extends Object>[] endpoints();

    int timeout() default 30;
}
