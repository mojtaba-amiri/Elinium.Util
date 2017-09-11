package com.elinium.util.ui.broadcast;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by amiri on 9/7/2017.
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OnBroadcastReceived {
    enum Scope {LOCAL, PUBLIC}
    String actionName() default "";
    Scope scope() default Scope.PUBLIC;
}
