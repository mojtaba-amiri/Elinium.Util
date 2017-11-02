package com.elinium.util.retrofit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import okhttp3.Interceptor;

/**
 * Created by amiri on 9/17/2017.
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AddInterceptor {
    Class<? extends Interceptor>[] value();
}
