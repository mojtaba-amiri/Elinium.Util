package com.elinium.util.ui.fragment;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by amiri on 10/2/2017.
 */


public interface FragmentAction {
    void onFragmentActionRequest(String action, Object... data);
}