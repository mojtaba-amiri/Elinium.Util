package com.elinium.util.ui.layout;

import android.support.annotation.LayoutRes;

import com.elinium.util.ui.fragment.ETabFragment;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by amiri on 10/2/2017.
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface TabbedLayout {
    @LayoutRes int layout() default 0;

    Class<? extends ETabFragment>[] fragments();

    int pager() default 0;

    int toolbar() default 0;

    int tabLayout() default 0;

    int fab() default 0;

    boolean fabVisible() default true;

    int windowFeature() default -1;

    boolean noTitle() default false;

    boolean transparent() default false;

    boolean fullScreen() default false;


}
