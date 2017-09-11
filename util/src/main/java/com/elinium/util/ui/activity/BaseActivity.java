package com.elinium.util.ui.activity;

import android.arch.lifecycle.LifecycleActivity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.elinium.util.exceptionhandling.ExceptionHandler;
import com.elinium.util.broadcast.BroadcastListener;

/**
 * Created by amiri on 9/6/2017.
 */

public abstract class BaseActivity extends LifecycleActivity implements ExceptionHandler.IExceptionHandler {
    public final String TAG = getClass().getSimpleName();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            BroadcastListener.initialize(this);
            ExceptionHandler.register(this);
        } catch (Exception e) {
            Log.e(TAG, "setDefaultUncaughtExceptionHandler error:" + e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onException(String threadName, Throwable throwable) {
        if (throwable != null) Log.e(TAG, "BaseActivity.onException:" + throwable.getMessage());
    }
}
