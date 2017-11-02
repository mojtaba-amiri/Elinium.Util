package com.elinium.util.ui.layout;

import android.support.annotation.LayoutRes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by mojtabaa on 2017-09-11.
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Layout {
    @LayoutRes int id() default 0;

    int windowFeature() default -1;

    boolean noTitle() default false;

    boolean transparent() default false;

    boolean fullScreen() default false;


}